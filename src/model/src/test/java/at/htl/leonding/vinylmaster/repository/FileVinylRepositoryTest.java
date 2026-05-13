package at.htl.leonding.vinylmaster.repository;

import at.htl.leonding.vinylmaster.model.Vinyl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileVinylRepositoryTest {

    private FileVinylRepository repository;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = new FileVinylRepository(tempDir.toString());
    }

    @Test
    void testSaveAndFindById() {
        Vinyl vinyl = new Vinyl("Test Album", "Test Artist", "Rock", 2020, new BigDecimal("19.99"));
        
        Vinyl saved = repository.save(vinyl);
        
        assertNotNull(saved.getId());
        assertEquals("Test Album", saved.getTitle());
        assertEquals("Test Artist", saved.getArtist());
        assertEquals(2020, saved.getYear());
    }

    @Test
    void testFindAll() {
        Vinyl vinyl1 = new Vinyl("Album 1", "Artist 1", "Rock", 2020, new BigDecimal("19.99"));
        Vinyl vinyl2 = new Vinyl("Album 2", "Artist 2", "Pop", 2021, new BigDecimal("15.99"));
        
        repository.save(vinyl1);
        repository.save(vinyl2);
        
        List<Vinyl> all = repository.findAll();
        
        assertTrue(all.size() >= 2);
    }

    @Test
    void testDataPersistence() {
        Vinyl vinyl = new Vinyl("Persistent Album", "Persistent Artist", "Jazz", 2019, new BigDecimal("24.99"));
        Vinyl saved = repository.save(vinyl);
        Long id = saved.getId();
        
        // Create new repository instance (simulates app restart)
        FileVinylRepository newRepository = new FileVinylRepository(tempDir.toString());
        var found = newRepository.findById(id);
        
        assertTrue(found.isPresent());
        assertEquals("Persistent Album", found.get().getTitle());
    }

    @Test
    void testFileIsCreated() {
        repository.save(new Vinyl("Any Album", "Any Artist", "Rock", 2020, new BigDecimal("10.00")));
        
        assertTrue(Files.exists(tempDir.resolve("vinyls.json")));
    }
}
