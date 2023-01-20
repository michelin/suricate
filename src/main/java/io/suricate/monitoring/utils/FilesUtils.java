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

package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.entities.Asset;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class FilesUtils {
    private FilesUtils() { }

    /**
     * Method used to list all folder inside a root folder
     * @param rootFolder the root folder used to find folder
     * @return the list of folder
     * @throws IOException exception with file
     */
    public static List<File> getFolders(File rootFolder) throws IOException {
        if (rootFolder != null) {
            try (Stream<Path> list = Files.list(rootFolder.toPath())) {
                return list.map(Path::toFile)
                    .filter(File::isDirectory)
                    .sorted()
                    .collect(Collectors.toList());

            }
        }
        return Collections.emptyList();
    }

    /**
     * Get all the files inside a given folder
     *
     * @param folder The folder containing the files
     * @return The list of files
     * @throws IOException Exception triggered during the files fetching
     */
    public static List<File> getFiles(File folder) throws IOException {
        if (folder != null) {
            try (Stream<Path> list = Files.list(folder.toPath())) {
                return list.map(Path::toFile)
                    .filter(File::isFile)
                    .sorted()
                    .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Method used to read File asset
     *
     * @param file the file asset to read
     * @return the asset corresponding to the file content
     * @throws IOException file read exception
     */
    public static Asset readAsset(File file) throws IOException {
        Asset asset = new Asset();
        asset.setContent(FileUtils.readFileToByteArray(file));
        try {
            MagicMatch match = Magic.getMagicMatch(asset.getContent());
            asset.setContentType(match.getMimeType());
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            log.trace(e.getMessage(), e);
            asset.setContentType("text/plain");
        }

        // Override mime type for javascript
        if (file.getName().endsWith(".js")) {
            asset.setContentType("application/javascript");
        }

        return asset;
    }
}
