package at.htl.leonding.vinylmaster.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileGenreRepository implements GenreRepository {
    private static final String DATA_DIR = "data";
    private static final String GENRES_FILE = "genres.json";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path dataFilePath;
    private Set<String> genres;

    public FileGenreRepository() {
        this.dataFilePath = Paths.get(DATA_DIR, GENRES_FILE);
        initializeDataDirectory();
        loadGenres();
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

    private void loadGenres() {
        try {
            if (Files.exists(dataFilePath)) {
                String content = Files.readString(dataFilePath);
                String[] genreArray = objectMapper.readValue(content, String[].class);
                genres = new HashSet<>(List.of(genreArray));
            } else {
                genres = new HashSet<>();
                saveGenres();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load genres from file", e);
        }
    }

    private void saveGenres() {
        try {
            List<String> sortedGenres = genres.stream().sorted().toList();
            String json = objectMapper.writeValueAsString(sortedGenres);
            Files.writeString(dataFilePath, json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save genres to file", e);
        }
    }

    @Override
    public List<String> findAll() {
        return genres.stream().sorted().toList();
    }

    @Override
    public void save(String genre) {
        if (genre != null && !genre.trim().isEmpty()) {
            genres.add(genre.trim());
            saveGenres();
        }
    }
}
