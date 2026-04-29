package at.htl.leonding.vinylmaster.service;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.repository.VinylRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VinylServiceImplTest {
    private VinylServiceImpl vinylService;
    private TestVinylRepository testRepository;

    @BeforeEach
    void setUp() {
        testRepository = new TestVinylRepository();
        vinylService = new VinylServiceImpl(testRepository);
    }

    @Test
    void testAddValidVinyl() throws Exception {
        Vinyl vinyl = new Vinyl("Dark Side of the Moon", "Pink Floyd", "Rock", 1973, new BigDecimal("29.99"));
        
        Vinyl saved = vinylService.addVinyl(vinyl);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Dark Side of the Moon", saved.getTitle());
        assertEquals("Pink Floyd", saved.getArtist());
        assertEquals(1973, saved.getYear());
    }

    @Test
    void testAddVinylWithMissingTitle() {
        Vinyl vinyl = new Vinyl(null, "Pink Floyd", "Rock", 1973, new BigDecimal("29.99"));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vinylService.addVinyl(vinyl);
        });
        
        assertEquals("title", exception.getFieldName());
    }

    @Test
    void testAddVinylWithMissingArtist() {
        Vinyl vinyl = new Vinyl("Dark Side of the Moon", "", "Rock", 1973, new BigDecimal("29.99"));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vinylService.addVinyl(vinyl);
        });
        
        assertEquals("artist", exception.getFieldName());
    }

    @Test
    void testAddVinylWithMissingYear() {
        Vinyl vinyl = new Vinyl("Dark Side of the Moon", "Pink Floyd", "Rock", null, new BigDecimal("29.99"));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vinylService.addVinyl(vinyl);
        });
        
        assertEquals("year", exception.getFieldName());
    }

    @Test
    void testAddVinylWithInvalidYear() {
        Vinyl vinyl = new Vinyl("Dark Side of the Moon", "Pink Floyd", "Rock", 1800, new BigDecimal("29.99"));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vinylService.addVinyl(vinyl);
        });
        
        assertEquals("year", exception.getFieldName());
    }

    @Test
    void testAddVinylWithNegativePrice() {
        Vinyl vinyl = new Vinyl("Dark Side of the Moon", "Pink Floyd", "Rock", 1973, new BigDecimal("-10.00"));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vinylService.addVinyl(vinyl);
        });
        
        assertEquals("price", exception.getFieldName());
    }

    @Test
    void testAddDuplicateVinyl() throws Exception {
        Vinyl vinyl1 = new Vinyl("Dark Side of the Moon", "Pink Floyd", "Rock", 1973, new BigDecimal("29.99"));
        vinylService.addVinyl(vinyl1);
        
        Vinyl vinyl2 = new Vinyl("Dark Side of the Moon", "Pink Floyd", "Rock", 1973, new BigDecimal("25.00"));
        
        assertThrows(DuplicateVinylException.class, () -> {
            vinylService.addVinyl(vinyl2);
        });
    }

    @Test
    void testGetAllVinyls() throws Exception {
        vinylService.addVinyl(new Vinyl("Album 1", "Artist 1", "Rock", 2000, new BigDecimal("20.00")));
        vinylService.addVinyl(new Vinyl("Album 2", "Artist 2", "Jazz", 2010, new BigDecimal("25.00")));
        
        List<Vinyl> vinyls = vinylService.getAllVinyls();
        
        assertEquals(2, vinyls.size());
    }

    @Test
    void testUpdateVinylDuplicateAgainstOtherRecord() throws Exception {
        Vinyl first = vinylService.addVinyl(new Vinyl("Album 1", "Artist 1", "Rock", 2000, new BigDecimal("20.00")));
        Vinyl second = vinylService.addVinyl(new Vinyl("Album 2", "Artist 2", "Jazz", 2010, new BigDecimal("25.00")));

        second.setTitle(first.getTitle());
        second.setArtist(first.getArtist());
        second.setYear(first.getYear());

        assertThrows(DuplicateVinylException.class, () -> vinylService.updateVinyl(second));
    }

    @Test
    void testUpdateVinylKeepsSameValues() throws Exception {
        Vinyl vinyl = vinylService.addVinyl(new Vinyl("Album 1", "Artist 1", "Rock", 2000, new BigDecimal("20.00")));

        Vinyl updated = vinylService.updateVinyl(vinyl);

        assertEquals(vinyl.getId(), updated.getId());
    }

    // Test repository implementation
    static class TestVinylRepository implements VinylRepository {
        private final List<Vinyl> vinyls = new ArrayList<>();
        private long idCounter = 1;

        @Override
        public Vinyl save(Vinyl vinyl) {
            if (vinyl.getId() == null) {
                vinyl.setId(idCounter++);
                vinyls.add(vinyl);
            }
            return vinyl;
        }

        @Override
        public List<Vinyl> findAll() {
            return new ArrayList<>(vinyls);
        }

        @Override
        public Optional<Vinyl> findById(Long id) {
            return vinyls.stream().filter(v -> v.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Vinyl> findByTitleAndArtistAndYear(String title, String artist, Integer year) {
            return vinyls.stream()
                    .filter(v -> v.getTitle().equals(title) && 
                                 v.getArtist().equals(artist) && 
                                 v.getYear().equals(year))
                    .findFirst();
        }

        @Override
        public boolean existsByTitleAndArtistAndYear(String title, String artist, Integer year) {
            return findByTitleAndArtistAndYear(title, artist, year).isPresent();
        }

        @Override
        public boolean existsByTitleAndArtistAndYearAndIdNot(String title, String artist, Integer year, Long excludedId) {
            return vinyls.stream()
                    .anyMatch(v -> !v.getId().equals(excludedId)
                            && v.getTitle().equals(title)
                            && v.getArtist().equals(artist)
                            && v.getYear().equals(year));
        }

        @Override
        public void deleteById(Long id) {
            vinyls.removeIf(v -> v.getId().equals(id));
        }
    }
}
