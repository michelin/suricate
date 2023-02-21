/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.michelin.suricate.model.entities.Category;
import com.michelin.suricate.model.entities.Library;
import com.michelin.suricate.model.entities.Widget;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public final class WidgetUtils {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Method used to parse library folder
     *
     * @param rootFolder the root library folder
     * @return the list of library
     */
    public static List<Library> parseLibraryFolder(File rootFolder) throws IOException {
        List<Library> libraries = null;

        List<File> list = FilesUtils.getFiles(rootFolder);

        if (!list.isEmpty()) {
            libraries = new ArrayList<>();

            for (File file : list) {
                Library lib = new Library();
                lib.setAsset(FilesUtils.readAsset(file));
                lib.setTechnicalName(file.getName());
                libraries.add(lib);
            }
        }

        return libraries;
    }

    /**
     * Method used to parse category folder
     *
     * @param rootFolder the folder to parse
     * @return the list of category to parse
     */
    public static List<Category> parseCategoriesFolder(File rootFolder) {
        List<Category> categories = new ArrayList<>();

        try {
            List<File> list = FilesUtils.getFolders(rootFolder);

            if (list.isEmpty()) {
                return Collections.emptyList();
            }

            for (File folderCategory : list) {
                Category category = WidgetUtils.getCategory(folderCategory);

                if (category != null) {
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return categories;
    }

    /**
     * Method used to get category from Folder
     *
     * @param folderCategory folder category
     * @return the category bean
     * @throws IOException Triggered exception during the files reading
     */
    public static Category getCategory(File folderCategory) throws IOException {
        if (folderCategory == null) {
            return null;
        }

        Category category = new Category();
        List<File> files = FilesUtils.getFiles(folderCategory);

        if (files.isEmpty()) {
            return category;
        }

        for (File file : files) {
            if ("icon".equals(FilenameUtils.getBaseName(file.getName()))) {
                category.setImage(FilesUtils.readAsset(file));
            } else if ("description".equals(FilenameUtils.getBaseName(file.getName()))) {
                mapper.readerForUpdating(category).readValue(file);
            }
        }

        // Avoid not well formatted category
        if (StringUtils.isBlank(category.getName())) {
            log.error("Category {} invalid it's name must not be empty", folderCategory.getPath());
            return null;
        }

        File widgetRootFolder = new File(folderCategory.getPath() + File.separator + "widgets" + File.separator);

        if (widgetRootFolder.exists()) {
            Set<Widget> widgets = new LinkedHashSet<>();
            List<File> folders = FilesUtils.getFolders(widgetRootFolder);

            for (File widgetFolder : folders) {
                Widget widget = WidgetUtils.getWidget(widgetFolder);

                if (widget != null) {
                    widgets.add(widget);
                }
            }

            category.setWidgets(widgets);
        }

        return category;
    }

    /**
     * Get widget from a given folder
     *
     * @param folder The folder from which to retrieve the widget
     * @return The built widget from the folder
     * @throws IOException Triggered exception during the widget files reading
     */
    public static Widget getWidget(File folder) throws IOException {
        if (folder == null) {
            return null;
        }

        Widget widget = new Widget();
        List<File> files = FilesUtils.getFiles(folder);

        if (!files.isEmpty()) {
            for (File file : files) {
                WidgetUtils.readWidgetConfig(widget, file);
            }

            if (widget.getDelay() == null) {
                log.error("Widget delay must not be null : {}", folder.getPath());
                return null;
            }

            if (widget.getDelay() > 0 && StringUtils.isBlank(widget.getBackendJs())) {
                log.error("Widget script must not be empty when delay > 0 : {}", folder.getPath());
                return null;
            }

            if (StringUtils.isAnyBlank(widget.getCssContent(), widget.getDescription(), widget.getHtmlContent(), widget.getTechnicalName(), widget.getName())) {
                log.error("Widget is not well formatted : {}", folder.getPath());
                return null;
            }
        }

        return widget;
    }

    /**
     * Read the given file. According to the name of the file,
     * fill the widget with the information contained in the file
     *
     * @param widget The widget
     * @param file The file containing information to set to the widget
     * @throws IOException Exception triggered during file reading
     */
    private static void readWidgetConfig(Widget widget, File file) throws IOException {
        if ("image".equals(FilenameUtils.getBaseName(file.getName()))) {
            widget.setImage(FilesUtils.readAsset(file));
        } else if ("description".equals(FilenameUtils.getBaseName(file.getName()))) {
            mapper.readerForUpdating(widget).readValue(file);
        } else if ("script".equals(FilenameUtils.getBaseName(file.getName()))) {
            widget.setBackendJs(StringUtils.trimToNull(FileUtils.readFileToString(file, StandardCharsets.UTF_8)));
        } else if ("style".equals(FilenameUtils.getBaseName(file.getName()))) {
            widget.setCssContent(StringUtils.trimToNull(FileUtils.readFileToString(file, StandardCharsets.UTF_8)));
        } else if ("content".equals(FilenameUtils.getBaseName(file.getName()))) {
            widget.setHtmlContent(StringUtils.trimToNull(FileUtils.readFileToString(file, StandardCharsets.UTF_8)));
        } else if ("params".equals(FilenameUtils.getBaseName(file.getName()))) {
            mapper.readerForUpdating(widget).readValue(file);
        }
    }

    private WidgetUtils() { }
}
