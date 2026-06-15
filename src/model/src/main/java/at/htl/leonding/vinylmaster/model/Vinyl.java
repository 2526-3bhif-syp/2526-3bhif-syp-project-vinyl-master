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
    private String imagePath;
    private String storageLocation;
    private String notes;
    private boolean favorite;
    private boolean wantlist;
    private String condition;

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
        this(id, title, artist, genre, year, price, null, null, null);
    }

    public Vinyl(Long id, String title, String artist, String genre, Integer year, BigDecimal price, String imagePath) {
        this(id, title, artist, genre, year, price, imagePath, null, null);
    }

    public Vinyl(Long id, String title, String artist, String genre, Integer year, BigDecimal price,
                 String imagePath, String storageLocation, String notes) {
        this(id, title, artist, genre, year, price, imagePath, storageLocation, notes, false, false, null);
    }

    public Vinyl(Long id, String title, String artist, String genre, Integer year, BigDecimal price,
                 String imagePath, String storageLocation, String notes, boolean favorite, boolean wantlist, String condition) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.year = year;
        this.price = price;
        this.imagePath = imagePath;
        this.storageLocation = storageLocation;
        this.notes = notes;
        this.favorite = favorite;
        this.wantlist = wantlist;
        this.condition = condition;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isWantlist() {
        return wantlist;
    }

    public void setWantlist(boolean wantlist) {
        this.wantlist = wantlist;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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
                ", imagePath='" + imagePath + '\'' +
                ", storageLocation='" + storageLocation + '\'' +
                ", notes='" + notes + '\'' +
                ", favorite=" + favorite +
                ", wantlist=" + wantlist +
                ", condition='" + condition + '\'' +
                '}';
    }
}
