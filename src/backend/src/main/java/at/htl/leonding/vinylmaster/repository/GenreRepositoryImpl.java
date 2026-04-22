package at.htl.leonding.vinylmaster.repository;

import java.util.List;

public class GenreRepositoryImpl implements GenreRepository {
    private static final GenreRepository fileRepository = new FileGenreRepository();

    @Override
    public List<String> findAll() {
        return fileRepository.findAll();
    }

    @Override
    public void save(String genre) {
        fileRepository.save(genre);
    }
}

