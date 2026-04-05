package at.htl.leonding.vinylmaster.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Vinyl {
    private Long id;
    private String title;
    private String artist;
    private String genre;
    private Integer year;
    private BigDecimal price;

    public Vinyl() {
    }

    public Vinyl(String title, String artist, String genre, Integer year, BigDecimal price) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.year = year;
        this.price = price;
    }

    public Vinyl(Long id, String title, String artist, String genre, Integer year, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.year = year;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vinyl vinyl = (Vinyl) o;
        return Objects.equals(id, vinyl.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vinyl{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", price=" + price +
                '}';
    }
}
