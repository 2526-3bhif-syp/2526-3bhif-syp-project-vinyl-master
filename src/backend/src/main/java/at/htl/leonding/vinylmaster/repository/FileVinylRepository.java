package at.htl.leonding.vinylmaster.repository;

import at.htl.leonding.vinylmaster.model.Vinyl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FileVinylRepository implements VinylRepository {
    private static final String DATA_DIR = "data";
    private static final String VINYLS_FILE = "vinyls.json";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path dataFilePath;
    private final AtomicLong idCounter;
    private List<Vinyl> vinyls;

    public FileVinylRepository() {
        this.dataFilePath = Paths.get(DATA_DIR, VINYLS_FILE);
        this.idCounter = new AtomicLong(0);
        initializeDataDirectory();
        loadVinyls();
        initializeIdCounter();
    }

    private void initializeDataDirectory() {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory", e);
        }
    }

    private void loadVinyls() {
        try {
            if (Files.exists(dataFilePath)) {
                String content = Files.readString(dataFilePath);
                Vinyl[] vinylArray = objectMapper.readValue(content, Vinyl[].class);
                vinyls = new ArrayList<>(List.of(vinylArray));
            } else {
                vinyls = new ArrayList<>();
                saveVinyls();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vinyls from file", e);
        }
    }

    private void saveVinyls() {
        try {
            String json = objectMapper.writeValueAsString(vinyls);
            Files.writeString(dataFilePath, json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save vinyls to file", e);
        }
    }

    private void initializeIdCounter() {
        vinyls.forEach(vinyl -> {
            if (vinyl.getId() != null && vinyl.getId() > idCounter.get()) {
                idCounter.set(vinyl.getId());
            }
        });
    }

    @Override
    public Vinyl save(Vinyl vinyl) {
        if (vinyl.getId() == null) {
            vinyl.setId(idCounter.incrementAndGet());
            vinyls.add(vinyl);
        } else {
            vinyls.stream()
                    .filter(v -> v.getId().equals(vinyl.getId()))
                    .findFirst()
                    .ifPresent(existing -> {
                        existing.setTitle(vinyl.getTitle());
                        existing.setArtist(vinyl.getArtist());
                        existing.setGenre(vinyl.getGenre());
                        existing.setYear(vinyl.getYear());
                        existing.setPrice(vinyl.getPrice());
                        existing.setImagePath(vinyl.getImagePath());
                        existing.setStorageLocation(vinyl.getStorageLocation());
                        existing.setNotes(vinyl.getNotes());
                    });
        }
        saveVinyls();
        return vinyl;
    }

    @Override
    public List<Vinyl> findAll() {
        return new ArrayList<>(vinyls);
    }

    @Override
    public Optional<Vinyl> findById(Long id) {
        return vinyls.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
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
                .filter(v -> v.getTitle().equals(title) && 
                           v.getArtist().equals(artist) && 
                           v.getYear().equals(year) && 
                           !v.getId().equals(excludedId))
                .findFirst()
                .isPresent();
    }

    @Override
    public void deleteById(Long id) {
        vinyls.removeIf(v -> v.getId().equals(id));
        saveVinyls();
    }
}
