package com.michelin.suricate.utils;

import com.michelin.suricate.model.entities.Asset;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FilesUtilsTest {

    @Test
    void shouldGetFolders() throws IOException {
        List<File> actual = FilesUtils.getFolders(new File("src/test/resources/repository"));

        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getName()).contains("content");
        assertThat(actual.get(1).getName()).contains("libraries");
    }

    @Test
    void shouldGetNoFolder() throws IOException {
        List<File> actual = FilesUtils.getFolders(null);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldGetFiles() throws IOException {
        List<File> actual = FilesUtils.getFiles(new File("src/test/resources/repository/libraries"));

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getName()).contains("test.js");
    }

    @Test
    void shouldGetNoFile() throws IOException {
        List<File> actual = FilesUtils.getFiles(null);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReadJSAsset() throws IOException {
        Asset actual = FilesUtils.readAsset(new File("src/test/resources/repository/libraries/test.js"));

        assertThat(actual.getContentType()).isEqualTo("application/javascript");
        assertThat(actual.getSize()).isEqualTo(9);
        assertThat(actual.getContent()).isEqualTo(new byte[]{108, 101, 116, 32, 116, 101, 115, 116, 59});
    }
}
