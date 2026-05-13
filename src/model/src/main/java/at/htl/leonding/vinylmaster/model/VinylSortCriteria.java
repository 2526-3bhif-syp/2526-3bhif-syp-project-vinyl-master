package at.htl.leonding.vinylmaster.model;

import java.math.BigDecimal;
import java.util.Comparator;

public enum VinylSortCriteria {

    TITLE_ASC("Title A–Z"),
    TITLE_DESC("Title Z–A"),
    ARTIST_ASC("Artist A–Z"),
    ARTIST_DESC("Artist Z–A"),
    PRICE_ASC("Price ↑"),
    PRICE_DESC("Price ↓");

    private final String displayName;

    VinylSortCriteria(String displayName) {
        this.displayName = displayName;
    }

    public Comparator<Vinyl> toComparator() {
        return switch (this) {
            case TITLE_ASC  -> Comparator.comparing(Vinyl::getTitle,  Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case TITLE_DESC -> Comparator.comparing(Vinyl::getTitle,  Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()));
            case ARTIST_ASC  -> Comparator.comparing(Vinyl::getArtist, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case ARTIST_DESC -> Comparator.comparing(Vinyl::getArtist, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()));
            case PRICE_ASC  -> Comparator.comparing(Vinyl::getPrice,  Comparator.nullsLast(Comparator.naturalOrder()));
            case PRICE_DESC -> Comparator.comparing(Vinyl::getPrice,  Comparator.nullsLast(Comparator.reverseOrder()));
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
