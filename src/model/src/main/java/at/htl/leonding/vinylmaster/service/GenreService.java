package at.htl.leonding.vinylmaster.service;

import at.htl.leonding.vinylmaster.repository.GenreRepository;
import at.htl.leonding.vinylmaster.repository.GenreRepositoryImpl;

import java.util.List;

public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService() {
        this.genreRepository = new GenreRepositoryImpl();
    }

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<String> getAllGenres() {
        return genreRepository.findAll();
    }

    public void addGenre(String genre) {
        if (genre != null && !genre.trim().isEmpty()) {
            genreRepository.save(genre.trim());
        }
    }
}
