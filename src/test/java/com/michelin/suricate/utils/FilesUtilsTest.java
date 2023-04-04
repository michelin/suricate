package com.michelin.suricate.utils;

import com.michelin.suricate.model.entities.Asset;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

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

        assertThat(actual).isNotNull();
        assertThat(actual.getContentType()).isEqualTo("application/javascript");
    }

    @Test
    void shouldThrowExceptionWhenReadingJSAsset() throws IOException {
        try (MockedStatic<Magic> mocked = mockStatic(Magic.class)) {
            mocked.when(() -> Magic.getMagicMatch(any()))
                    .thenThrow(new MagicException("error"));

            Asset actual = FilesUtils.readAsset(new File("src/test/resources/repository/content/other/description.yml"));

            assertThat(actual.getContentType()).isEqualTo("text/plain");
            assertThat(actual.getSize()).isPositive();
            assertThat(actual.getContent()).isNotEmpty();
        }
    }
}
