package at.htl.leonding.vinylmaster.repository;

import at.htl.leonding.vinylmaster.model.Vinyl;

import java.util.List;
import java.util.Optional;

public interface VinylRepository {
    Vinyl save(Vinyl vinyl);
    List<Vinyl> findAll();
    Optional<Vinyl> findById(Long id);
    Optional<Vinyl> findByTitleAndArtistAndYear(String title, String artist, Integer year);
    boolean existsByTitleAndArtistAndYear(String title, String artist, Integer year);
    boolean existsByTitleAndArtistAndYearAndIdNot(String title, String artist, Integer year, Long excludedId);
    void deleteById(Long id);
}
