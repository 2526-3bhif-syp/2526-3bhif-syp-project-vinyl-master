package at.htl.leonding.vinylmaster.repository;

import at.htl.leonding.vinylmaster.config.DatabaseConfig;
import at.htl.leonding.vinylmaster.model.Vinyl;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VinylRepositoryImpl implements VinylRepository {

    @Override
    public Vinyl save(Vinyl vinyl) {
        if (vinyl.getId() == null) {
            return insert(vinyl);
        } else {
            return update(vinyl);
        }
    }

    private Vinyl insert(Vinyl vinyl) {
        String sql = "INSERT INTO vinyl (title, artist, genre, year, price) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vinyl.getTitle());
            stmt.setString(2, vinyl.getArtist());
            stmt.setString(3, vinyl.getGenre());
            stmt.setInt(4, vinyl.getYear());
            if (vinyl.getPrice() != null) {
                stmt.setBigDecimal(5, vinyl.getPrice());
            } else {
                stmt.setNull(5, Types.NUMERIC);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vinyl.setId(rs.getLong("id"));
                }
            }
            
            return vinyl;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert vinyl", e);
        }
    }

    private Vinyl update(Vinyl vinyl) {
        String sql = "UPDATE vinyl SET title = ?, artist = ?, genre = ?, year = ?, price = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vinyl.getTitle());
            stmt.setString(2, vinyl.getArtist());
            stmt.setString(3, vinyl.getGenre());
            stmt.setInt(4, vinyl.getYear());
            if (vinyl.getPrice() != null) {
                stmt.setBigDecimal(5, vinyl.getPrice());
            } else {
                stmt.setNull(5, Types.NUMERIC);
            }
            stmt.setLong(6, vinyl.getId());
            
            stmt.executeUpdate();
            return vinyl;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update vinyl", e);
        }
    }

    @Override
    public List<Vinyl> findAll() {
        String sql = "SELECT id, title, artist, genre, year, price FROM vinyl ORDER BY created_at DESC";
        List<Vinyl> vinyls = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vinyls.add(mapResultSetToVinyl(rs));
            }
            
            return vinyls;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch vinyls", e);
        }
    }

    @Override
    public Optional<Vinyl> findById(Long id) {
        String sql = "SELECT id, title, artist, genre, year, price FROM vinyl WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVinyl(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find vinyl by id", e);
        }
    }

    @Override
    public Optional<Vinyl> findByTitleAndArtistAndYear(String title, String artist, Integer year) {
        String sql = "SELECT id, title, artist, genre, year, price FROM vinyl WHERE title = ? AND artist = ? AND year = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setInt(3, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVinyl(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find vinyl", e);
        }
    }

    @Override
    public boolean existsByTitleAndArtistAndYear(String title, String artist, Integer year) {
        return findByTitleAndArtistAndYear(title, artist, year).isPresent();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM vinyl WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete vinyl", e);
        }
    }

    private Vinyl mapResultSetToVinyl(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String artist = rs.getString("artist");
        String genre = rs.getString("genre");
        Integer year = rs.getInt("year");
        BigDecimal price = rs.getBigDecimal("price");
        
        return new Vinyl(id, title, artist, genre, year, price);
    }
}
