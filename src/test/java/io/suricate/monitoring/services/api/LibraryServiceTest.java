package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Asset;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.repositories.AssetRepository;
import io.suricate.monitoring.repositories.LibraryRepository;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LibraryServiceTest {
    @Autowired
    LibraryService libraryService;

    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    AssetRepository assetRepository;

    @Test
    public void testUpdateLibraryNull() throws IOException {
        assertEquals(0, libraryRepository.count());
        assertEquals(0 ,assetRepository.count());
        assertEquals(0, libraryService.createUpdateLibraries(null).size());
        assertEquals(0, libraryRepository.count());
        assertEquals(0, assetRepository.count());
    }

    @Test
    public void testUpdateLibrary() throws IOException {
        assertEquals(0, libraryRepository.count());
        assertEquals(0 ,assetRepository.count());

        Library library = new Library();
        library.setTechnicalName("Technical name");

        Asset asset = new Asset();
        asset.setContentType("text/plain");
        asset.setContent(FileUtils.readFileToByteArray(new File(LibraryServiceTest.class.getResource("/libraries/d3.min.js").getFile())));
        library.setAsset(asset);

        libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertEquals(1, libraryRepository.count());

        Library libraryByTechnicalName = libraryRepository.findByTechnicalName("Technical name");
        assertNotNull(libraryByTechnicalName.getAsset());
        assertEquals(3, libraryByTechnicalName.getAsset().getSize());
        assertEquals(1, assetRepository.count());

        asset = new Asset();
        asset.setContentType("text/plain");
        asset.setContent(FileUtils.readFileToByteArray(new File(LibraryServiceTest.class.getResource("/libraries/d3.min.js").getFile())));
        asset.setSize(10);
        library.setAsset(asset);

        // Update same library
        libraryService.createUpdateLibraries(Collections.singletonList(library));
        assertEquals(1, libraryRepository.count());

        library = libraryRepository.findByTechnicalName("Technical name");

        assertNotNull(library.getAsset());
        assertEquals(10, library.getAsset().getSize());
        assertEquals(1, assetRepository.count());

        library = new Library();
        library.setTechnicalName("Technical name 2");

        asset = new Asset();
        asset.setContentType("text/plain");
        asset.setContent(FileUtils.readFileToByteArray(new File(LibraryServiceTest.class.getResource("/libraries/d3.min.js").getFile())));
        asset.setSize(10);
        library.setAsset(asset);

        libraryService.createUpdateLibraries(Collections.singletonList(library));
        assertEquals(2, libraryRepository.count());
        assertEquals(2, assetRepository.count());
    }
}
