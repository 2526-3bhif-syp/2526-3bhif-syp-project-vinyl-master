package at.htl.leonding.vinylmaster.repository;

import at.htl.leonding.vinylmaster.model.Vinyl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class VinylRepositoryImpl implements VinylRepository {
    private static final VinylRepository fileRepository = new FileVinylRepository();

    @Override
    public Vinyl save(Vinyl vinyl) {
        return fileRepository.save(vinyl);
    }

    @Override
    public List<Vinyl> findAll() {
        return fileRepository.findAll();
    }

    @Override
    public Optional<Vinyl> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Override
    public Optional<Vinyl> findByTitleAndArtistAndYear(String title, String artist, Integer year) {
        return fileRepository.findByTitleAndArtistAndYear(title, artist, year);
    }

    @Override
    public boolean existsByTitleAndArtistAndYear(String title, String artist, Integer year) {
        return fileRepository.existsByTitleAndArtistAndYear(title, artist, year);
    }

    @Override
    public boolean existsByTitleAndArtistAndYearAndIdNot(String title, String artist, Integer year, Long excludedId) {
        return fileRepository.existsByTitleAndArtistAndYearAndIdNot(title, artist, year, excludedId);
    }

    @Override
    public void deleteById(Long id) {
        fileRepository.deleteById(id);
    }
}

