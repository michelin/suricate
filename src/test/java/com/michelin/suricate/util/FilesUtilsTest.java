package com.michelin.suricate.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.entity.Asset;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

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
    void shouldReadJsAsset() throws IOException {
        Asset actual = FilesUtils.readAsset(new File("src/test/resources/repository/libraries/test.js"));

        assertThat(actual).isNotNull();
        assertThat(actual.getContentType()).isEqualTo("application/javascript");
    }

    @Test
    void shouldReadImageAsset() throws IOException {
        Asset actual = FilesUtils.readAsset(
            new File("src/test/resources/repository/content/github/widgets/count-issues/image.png"));

        assertThat(actual).isNotNull();
        assertThat(actual.getContentType()).isEqualTo("image/png");
    }

    @Test
    void shouldSetContentTypeToDefaultTextPlain() throws IOException {
        Asset actual =
            FilesUtils.readAsset(new File("src/test/resources/repository/content/other/description.yml"));

        assertThat(actual.getContentType()).isEqualTo("text/plain");
        assertThat(actual.getSize()).isPositive();
        assertThat(actual.getContent()).isNotEmpty();
    }
}
