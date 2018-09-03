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

package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository used for request Widget repository in database
 */
public interface WidgetRepository extends JpaRepository<Widget, Long> {

	/**
	 * Find a widget by technical name
	 *
	 * @param technicalname The technical name
	 * @return The related widget
	 */
	Widget findByTechnicalName(String technicalname);

	/**
	 * Method used to get image content from widget id
	 * @param id
	 * @return
	 */
	@Query("SELECT image FROM Widget WHERE id = :id")
	byte[] getImagebyWidgetId(@Param("id") Long id);


	/**
	 * Get a widget from the project widget Id
	 * @param projectWidgetId project widget id
	 * @param projectId project id
	 * @return teh widget
	 */
	@Query("SELECT w FROM Widget w, ProjectWidget pw WHERE w.id = pw.widget.id AND pw.id = :id AND pw.project.id = :projectId")
	Widget findByProjectWidgetId(@Param("id") Long projectWidgetId, @Param("projectId") Long projectId);


	/**
	 * update a widgetby technical name
	 *
	 * @param name The widget name
	 * @param description The widget description
	 * @param htmlContent The html content of the widget
	 * @param cssContent The css content of the widget
	 * @param backendJs The JS of the widget
	 * @param image The image
	 * @param info The user informations
	 * @param delay The refresh delay
	 * @param categoryId The related category id
	 * @param technicalName The technical name
	 * @return The state of the update
	 */
	@Modifying
	@Query("UPDATE Widget " +
			"SET name = :name, " +
			"description = :description, " +
			"htmlContent = :htmlContent, " +
			"cssContent = :cssContent, " +
			"backendJs = :backendJs, " +
			"delay = :delay, " +
			"image = :image, " +
			"info = :info, " +
			"category.id = :categoryId " +
			"WHERE technicalName = :technicalName")
	int updateWidget(@Param("name") String name,
					 @Param("description") String description,
					 @Param("htmlContent") String htmlContent,
					 @Param("cssContent") String cssContent,
					 @Param("backendJs") String backendJs,
					 @Param("image") Asset image,
					 @Param("info") String info,
					 @Param("delay") Long delay,
					 @Param("categoryId") Long categoryId,
					 @Param("technicalName") String technicalName);

	/**
	 * Find All by category name asc
	 *
	 * @return The list of widgets
	 */
	List<Widget> findAllByOrderByCategory_NameAsc();

	/**
	 * Method used to get all widget by Availability
	 * @param widgetAvailability the availability type
	 * @return the list of widgets
	 */
	List<Widget> findAllByWidgetAvailabilityOrderByNameAsc(WidgetAvailabilityEnum widgetAvailability);

	/**
	 * Find every widgets by category id
	 *
	 * @param categoryId The category id
	 * @return The list of related widgets ordered by name
	 */
	List<Widget> findAllByCategory_IdOrderByNameAsc(final Long categoryId);

}
