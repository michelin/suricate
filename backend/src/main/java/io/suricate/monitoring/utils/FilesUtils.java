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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FilesUtils {


    /**
     * Method used to list all folder inside a root folder
     * @param rootFolder the root folder used to find folder
     * @return the list of folder
     * @throws IOException exeception with file
     */
    public static List<File> getFolders(File rootFolder) throws IOException {
        if (rootFolder != null) {
            try (Stream<Path> list = Files.list(rootFolder.toPath())) {
                return list.filter(Files::isDirectory)
                        .map(Path::toFile)
                        .sorted()
                        .collect(Collectors.toList());

            }
        }
        return null;
    }

    /**
     * Method used to list all files inside a root folder
     * @param rootFolder the root folder used to find files
     * @return the list of folder
     * @throws IOException exception with file
     */
    public static List<File> getFiles(File rootFolder) throws IOException {
        if (rootFolder != null) {
            try (Stream<Path> list = Files.list(rootFolder.toPath())) {
                return list.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

    private FilesUtils() {
    }
}
