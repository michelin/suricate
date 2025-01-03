package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michelin.suricate.model.entity.Asset;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class FilesUtilsTest {

    @Test
    void shouldGetFolders() throws IOException {
        List<File> actual = FilesUtils.getFolders(new File("src/test/resources/repository"));

        assertEquals(2, actual.size());
        assertTrue(actual.get(0).getName().contains("content"));
        assertTrue(actual.get(1).getName().contains("libraries"));
    }

    @Test
    void shouldGetNoFolder() throws IOException {
        List<File> actual = FilesUtils.getFolders(null);

        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldGetFiles() throws IOException {
        List<File> actual = FilesUtils.getFiles(new File("src/test/resources/repository/libraries"));

        assertEquals(1, actual.size());
        assertTrue(actual.getFirst().getName().contains("test.js"));
    }

    @Test
    void shouldGetNoFile() throws IOException {
        List<File> actual = FilesUtils.getFiles(null);

        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldReadJsAsset() throws IOException {
        Asset actual = FilesUtils.readAsset(new File("src/test/resources/repository/libraries/test.js"));

        assertNotNull(actual);
        assertEquals("application/javascript", actual.getContentType());
    }

    @Test
    void shouldReadImageAsset() throws IOException {
        Asset actual = FilesUtils.readAsset(
            new File("src/test/resources/repository/content/github/widgets/count-issues/image.png")
        );

        assertNotNull(actual);
        assertEquals("image/png", actual.getContentType());
    }

    @Test
    void shouldSetContentTypeToDefaultTextPlain() throws IOException {
        Asset actual = FilesUtils.readAsset(new File("src/test/resources/repository/content/other/description.yml"));

        // Ubuntu 24.04 LTS returns "application/yaml"
        assertTrue(List.of("text/plain", "application/yaml").contains(actual.getContentType()));
        assertTrue(actual.getSize() > 0);
        assertTrue(actual.getContent().length > 0);
    }
}
