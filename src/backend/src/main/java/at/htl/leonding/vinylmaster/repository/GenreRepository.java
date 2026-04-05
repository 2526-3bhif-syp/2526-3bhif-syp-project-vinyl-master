package at.htl.leonding.vinylmaster.repository;

import java.util.List;

public interface GenreRepository {
    List<String> findAll();
    void save(String genre);
}
