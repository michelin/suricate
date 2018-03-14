/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.service;

import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.repository.ConfigurationRepository;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.WidgetService;
import io.suricate.monitoring.service.nashorn.NashornService;
import io.suricate.monitoring.service.nashorn.WidgetJob;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class WidgetExecutor implements Schedulable{

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetExecutor.class.getName());

    /**
     * The number of executor
     */
    private static final int EXECUTOR_POOL_SIZE = 60;
    /**
     * Start widget process immediately
     */
    private static final long SMALL_DELAY = 2L;

    /**
     * Widget start delay inclusive
     */
    private static final int START_DELAY_INCLUSIVE = 30;

    /**
     * Widget end delay exclusive
     */
    private static final int END_DELAY_EXCLUSIVE = 120;

    /**
     * thread executor service
     */
    private ScheduledThreadPoolExecutor scheduledExecutorService;
    private ScheduledThreadPoolExecutor scheduledExecutorServiceFuture;

    private ProjectWidgetService projectWidgetService;
    private NashornService nashornService;


    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;


    public WidgetExecutor(final ProjectWidgetService projectWidgetService, final NashornService nashornService) {
        this.projectWidgetService = projectWidgetService;
        this.nashornService = nashornService;
    }

    /**
     * Map containing all current scheduled jobs
     */
    private Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> jobs = new ConcurrentHashMap<>();

    /**
     * Method used to init scheduler
     */
    @Transactional
    public void initScheduler() {
        LOGGER.info("Init widget scheduler");

        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
        scheduledExecutorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        scheduledExecutorService.setRemoveOnCancelPolicy(true);

        if (scheduledExecutorServiceFuture != null) {
            scheduledExecutorServiceFuture.shutdownNow();
        }
        scheduledExecutorServiceFuture = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        scheduledExecutorServiceFuture.setRemoveOnCancelPolicy(true);
        // clear jobs
        jobs.clear();

        loadWidget();
    }

    /**
     * Method used to load and start all cron to update widget
     */
    protected void loadWidget(){
        LOGGER.debug("Load Widgets from database");
        projectWidgetService.resetProjectWidgetsState();

        List<NashornRequest> list = nashornService.getEveryNashornRequestFromDatabase();
        for (NashornRequest nashornRequest: list){
            try {
                schedule(nashornRequest, true, true);
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Method used to schedule widget update
     * @param nashornRequest nashorn request
     * @param startNow force widget update to start now
     * @param init force widget to update randomly between START_DELAY_INCLUSIVE and END_DELAY_EXCLUSIVE
     */
    public void schedule(final NashornRequest nashornRequest, boolean startNow, boolean init) {
        if (nashornRequest == null){
            return;
        }
        ProjectWidgetService projectWidgetService = ctx.getBean(ProjectWidgetService.class);

        if(!nashornService.isNashornRequestExecutable(nashornRequest)) {
            projectWidgetService.updateState(WidgetState.STOPPED, nashornRequest.getProjectWidgetId(), new Date());
            return;
        }

        // Update the status if necessary
        if (WidgetState.STOPPED == nashornRequest.getWidgetState()) {
            LOGGER.debug("Scheduled widget instance:{}", nashornRequest.getProjectWidgetId());
            projectWidgetService.updateState(WidgetState.RUNNING, nashornRequest.getProjectWidgetId(), new Date());
        }

        Long delay = nashornRequest.getDelay();
        if (init){
            // delay update on restart
            delay = RandomUtils.nextLong(START_DELAY_INCLUSIVE, END_DELAY_EXCLUSIVE);
        } else if (startNow) {
            delay = SMALL_DELAY;
        }

        // Add Global application config
        addGlobalConfiguration(nashornRequest);

        // Create scheduled future
        ScheduledFuture<NashornResponse> future = scheduledExecutorService.schedule(new WidgetJob(nashornRequest, stringEncryptor),
                delay,
                TimeUnit.SECONDS);

        // Create task
        ResultTask resultTask = ctx.getBean(ResultTask.class, future, nashornRequest, this);

        // future
        ScheduledFuture<Void> futureResult = scheduledExecutorServiceFuture.schedule(resultTask, delay, TimeUnit.SECONDS);

        // Update job
        jobs.put(nashornRequest.getProjectWidgetId(),
                new ImmutablePair<>(
                        new WeakReference<ScheduledFuture<NashornResponse>>(future),
                        new WeakReference<ScheduledFuture<Void>>(futureResult)
                ));

    }

    /**
     * Method used to cancelWidgetInstance the existing scheduled widget instance and launch a new instance
     * @param nashornRequest the nashorn request to execute
     */
    public void cancelAndSchedule(NashornRequest nashornRequest){
        cancelWidgetInstance(nashornRequest.getProjectWidgetId());
        schedule(nashornRequest, true, false);
    }

    /**
     * Method used to cancelWidgetInstance the existing scheduled widget instance
     * @param projectWidgetId the widget instance id
     */
    public void cancelWidgetInstance(Long projectWidgetId){
        Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>> pair = jobs.get(projectWidgetId);
        if (pair != null) {
            cancel(projectWidgetId, pair.getLeft());
            cancel(projectWidgetId, pair.getRight());
        }
    }


    /**
     * Method used to cancel a scheduled future for an widget instance
     * @param projectWidgetId project widget Id
     * @param weakReference weakReference containing the ScheduledFuture or null
     */
    private static void cancel(Long projectWidgetId, WeakReference< ? extends ScheduledFuture> weakReference) {
        if (weakReference != null) {
            ScheduledFuture scheduledFuture = weakReference.get();
            if (scheduledFuture != null && (!scheduledFuture.isDone() || !scheduledFuture.isCancelled())) {
                LOGGER.debug("Cancel task for widget instance {} ({})", projectWidgetId, scheduledFuture);
                scheduledFuture.cancel(true);
            }
        }
    }

    /**
     * Method used to inject global configuration to widget execution
     * @param nashornRequest the request to launch a job
     */
    private void addGlobalConfiguration(NashornRequest nashornRequest) {
        List<Configuration> configurations = configurationRepository.findConfigurationForWidget();
        if (configurations != null && !configurations.isEmpty()) {
            StringBuilder builder = new StringBuilder(nashornRequest.getProperties());
            builder.append('\n');
            for (Configuration configuration : configurations) {
                builder.append(configuration.getKey()).append('=').append(configuration.getValue()).append('\n');
            }
            nashornRequest.setProperties(builder.toString());
        }
    }
}
