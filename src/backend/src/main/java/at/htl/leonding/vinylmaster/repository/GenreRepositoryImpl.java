package at.htl.leonding.vinylmaster.repository;

import at.htl.leonding.vinylmaster.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreRepositoryImpl implements GenreRepository {

    @Override
    public List<String> findAll() {
        String sql = "SELECT name FROM genre ORDER BY name ASC";
        List<String> genres = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                genres.add(rs.getString("name"));
            }
            
            return genres;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch genres", e);
        }
    }

    @Override
    public void save(String genre) {
        String sql = "INSERT INTO genre (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, genre);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save genre", e);
        }
    }
}
