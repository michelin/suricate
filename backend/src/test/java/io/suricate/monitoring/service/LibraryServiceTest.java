package io.suricate.monitoring.service;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.repository.AssetRepository;
import io.suricate.monitoring.repository.LibraryRepository;
import io.suricate.monitoring.service.api.LibraryService;
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
import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

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
        // check empty
        assertThat(libraryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);

        assertThat(libraryService.updateLibraryInDatabase(null)).isNull();
        // check empty
        assertThat(libraryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);
    }

    @Test
    public void testUpdateLibrary() throws IOException {
        // check empty
        assertThat(libraryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);

        // Create test library
        Library lib = new Library();
        lib.setTechnicalName("test");

        Asset asset = new Asset();
        asset.setContentType("text/plain");
        asset.setContent(FileUtils.readFileToByteArray(new File(LibraryServiceTest.class.getResource("/Libraries/d3.min.js").getFile())));
        lib.setAsset(asset);
        // First update
        libraryService.updateLibraryInDatabase(Arrays.asList(lib));

        assertThat(libraryRepository.count()).isEqualTo(1);
        Library library = libraryRepository.findByTechnicalName("test");
        assertThat(library.getAsset()).isNotNull();
        assertThat(library.getAsset().getSize()).isEqualTo(3);
        assertThat(assetRepository.count()).isEqualTo(1);

        // Update asset size
        lib = new Library();
        lib.setTechnicalName("test");

        asset = new Asset();
        asset.setContentType("text/plain");
        asset.setContent(FileUtils.readFileToByteArray(new File(LibraryServiceTest.class.getResource("/Libraries/d3.min.js").getFile())));
        asset.setSize(10);
        lib.setAsset(asset);


        // Update same library
        libraryService.updateLibraryInDatabase(Arrays.asList(lib));
        assertThat(libraryRepository.count()).isEqualTo(1);
        library = libraryRepository.findByTechnicalName("test");
        assertThat(library.getAsset()).isNotNull();
        assertThat(library.getAsset().getSize()).isEqualTo(10);
        assertThat(assetRepository.count()).isEqualTo(1);

        // Add new lib
        lib = new Library();
        lib.setTechnicalName("test3");

        asset = new Asset();
        asset.setContentType("text/plain");
        asset.setContent(FileUtils.readFileToByteArray(new File(LibraryServiceTest.class.getResource("/Libraries/d3.min.js").getFile())));
        asset.setSize(10);
        lib.setAsset(asset);

        libraryService.updateLibraryInDatabase(Arrays.asList(lib));
        assertThat(libraryRepository.count()).isEqualTo(2);
        assertThat(assetRepository.count()).isEqualTo(2);
    }
}
