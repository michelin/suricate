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

package io.suricate.monitoring.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.Category;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.model.entity.Widget;
import net.sf.jmimemagic.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class WidgetUtils {

    /**
     * Object mapper for jackson
     */
    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetUtils.class);

    /**
     * Method used to parse library folder
     * @param rootFolder the root library folder
     * @return the list of library
     */
    public static List<Library> parseLibraryFolder(File rootFolder) {
        List<Library> libraries = null;
        try {
            List<File> list = FilesUtils.getFiles(rootFolder);
            if (list != null){
                libraries = new ArrayList<>();
                for (File file : list){
                    Library lib = new Library();
                    lib.setAsset(readAsset(file));
                    lib.setTechnicalName(file.getName());
                    libraries.add(lib);
                }
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        return libraries;
    }


    /**
     * Method used to parse category folder
     * @param rootFolder the folder to parse
     * @return the list of category to parse
     */
    public static List<Category> parseWidgetFolder(File rootFolder) {
        List<Category> categories = null;
        try {
            List<File> list = FilesUtils.getFolders(rootFolder);
            if (list == null) {
                return null;
            }
            categories = new ArrayList<>();
            for (File folderCategory : list){
                Category category = getCategory(folderCategory);
                if (category != null) {
                    categories.add(category);
                }
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        return categories;
    }

    /**
     * Method used to get category from Folder
     * @param folderCategory folder category
     * @return the category bean
     * @throws IOException
     */
    public static Category getCategory(File folderCategory) throws IOException {
        if (folderCategory == null ){
            return null;
        }
        Category category = new Category();
        List<File> files = FilesUtils.getFiles(folderCategory);
        if (files == null){
            return category;
        }
        for (File file: files){
            if ("icon".equals(FilenameUtils.getBaseName(file.getName()))) {
                category.setImage(readAsset(file));
            } else if ("description".equals(FilenameUtils.getBaseName(file.getName()))){
                mapper.readerForUpdating(category).readValue(file);
            }
        }
        // Avoid not well formed category
        if (StringUtils.isBlank(category.getName())){
            LOGGER.error("Category {} invalid it's name must not be empty",folderCategory.getPath());
            return null;
        }
        File widgetRootFolder = new File(folderCategory.getPath() + SystemUtils.FILE_SEPARATOR + "widgets" + SystemUtils.FILE_SEPARATOR);
        if (widgetRootFolder.exists()){
            ArrayList<Widget> widgets = new ArrayList<>();
            List<File> folders = FilesUtils.getFolders(widgetRootFolder);
            for (File widgetFolder : folders) {
                Widget widget = getWidget(widgetFolder);
                if (widget != null){
                    widgets.add(widget);
                }
            }
            category.setWidgets(widgets);
        }
        return category;
    }

    /**
     * Method used to get widget from Folder
     * @param widgetFolder widget folder
     * @return the Widget bean
     * @throws IOException
     */
    public static Widget getWidget(File widgetFolder) throws IOException {
        if (widgetFolder == null ){
            return null;
        }
        Widget widget = new Widget();
        List<File> files = FilesUtils.getFiles(widgetFolder);
        if (files != null){
            for (File file: files){
                readWidgetConfig(widget, file);
            }
            if (widget.getDelay() == null) {
                LOGGER.error("Widget delay must no be null : {}", widgetFolder.getPath());
                return null;
            }
            if (widget.getDelay() > 0 && StringUtils.isBlank(widget.getBackendJs())) {
                LOGGER.error("Widget script must not be empty when delay > 0 : {}", widgetFolder.getPath());
                return null;
            }

            if (StringUtils.isAnyBlank(widget.getCssContent(), widget.getDescription(), widget.getHtmlContent(), widget.getTechnicalName(), widget.getName())) {
                LOGGER.error("Widget is not well formatted : {}", widgetFolder.getPath());
                return null;
            }
        }
        return widget;
    }

    /**
     * Method used to read and extract all widget content from a file
     * @param widget the widget object
     * @param file the widget configuration folder
     * @throws IOException Exception during file read
     */
    private static void readWidgetConfig(Widget widget, File file) throws IOException {
        if ("image".equals(FilenameUtils.getBaseName(file.getName()))) {
            widget.setImage(readAsset(file));
        } else if ("description".equals(FilenameUtils.getBaseName(file.getName()))){
            mapper.readerForUpdating(widget).readValue(file);
        } else if ("script".equals(FilenameUtils.getBaseName(file.getName()))){
            widget.setBackendJs(StringUtils.trimToNull(FileUtils.readFileToString(file, StandardCharsets.UTF_8)));
        } else if ("style".equals(FilenameUtils.getBaseName(file.getName()))){
            widget.setCssContent(StringUtils.trimToNull(FileUtils.readFileToString(file, StandardCharsets.UTF_8)));
        } else if ("content".equals(FilenameUtils.getBaseName(file.getName()))){
            widget.setHtmlContent(StringUtils.trimToNull(FileUtils.readFileToString(file, StandardCharsets.UTF_8)));
        } else if ("params".equals(FilenameUtils.getBaseName(file.getName()))) {
            mapper.readerForUpdating(widget).readValue(file);
        }
    }

    /**
     * Method used to read File asset
     * @param file the file asset to read
     * @return the asset corresponding to the file content
     * @throws IOException file read exception
     */
    public static Asset readAsset(File file) throws IOException {
        Asset asset = new Asset();
        asset.setContent(FileUtils.readFileToByteArray(file));
        asset.setLastUpdateDate(new Date(file.lastModified()));
        try {
            MagicMatch match = Magic.getMagicMatch(asset.getContent());
            asset.setContentType(match.getMimeType());
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            LOGGER.trace(e.getMessage(), e);
            asset.setContentType("text/plain");
        }

        // Override mime type for javascript
        if (file.getName().endsWith(".js")){
            asset.setContentType("application/javascript");
        }

        return asset;
    }

    /**
     * Private constructor
     */
    private WidgetUtils() {
    }
}
