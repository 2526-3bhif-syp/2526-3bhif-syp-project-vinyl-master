package at.htl.leonding.vinylmaster.service;

import at.htl.leonding.vinylmaster.model.Vinyl;
import at.htl.leonding.vinylmaster.repository.VinylRepository;
import at.htl.leonding.vinylmaster.repository.VinylRepositoryImpl;

import java.util.List;

public class VinylServiceImpl implements VinylService {
    private final VinylRepository vinylRepository;

    public VinylServiceImpl() {
        this.vinylRepository = new VinylRepositoryImpl();
    }

    public VinylServiceImpl(VinylRepository vinylRepository) {
        this.vinylRepository = vinylRepository;
    }

    @Override
    public Vinyl addVinyl(Vinyl vinyl) throws DuplicateVinylException, ValidationException {
        validateVinyl(vinyl);
        checkForDuplicates(vinyl);
        return vinylRepository.save(vinyl);
    }

    @Override
    public List<Vinyl> getAllVinyls() {
        return vinylRepository.findAll();
    }

    @Override
    public Vinyl getVinylById(Long id) {
        return vinylRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vinyl not found with id: " + id));
    }

    @Override
    public void deleteVinyl(Long id) {
        vinylRepository.deleteById(id);
    }

    private void validateVinyl(Vinyl vinyl) throws ValidationException {
        if (vinyl.getTitle() == null || vinyl.getTitle().trim().isEmpty()) {
            throw new ValidationException("title", "Title is required");
        }
        
        if (vinyl.getArtist() == null || vinyl.getArtist().trim().isEmpty()) {
            throw new ValidationException("artist", "Artist is required");
        }
        
        if (vinyl.getYear() == null) {
            throw new ValidationException("year", "Year is required");
        }
        
        if (vinyl.getYear() < 1900 || vinyl.getYear() > 2100) {
            throw new ValidationException("year", "Year must be between 1900 and 2100");
        }
        
        if (vinyl.getPrice() != null && vinyl.getPrice().signum() < 0) {
            throw new ValidationException("price", "Price cannot be negative");
        }
    }

    private void checkForDuplicates(Vinyl vinyl) throws DuplicateVinylException {
        if (vinylRepository.existsByTitleAndArtistAndYear(
                vinyl.getTitle(), vinyl.getArtist(), vinyl.getYear())) {
            throw new DuplicateVinylException(
                    String.format("Vinyl already exists: %s by %s (%d)", 
                            vinyl.getTitle(), vinyl.getArtist(), vinyl.getYear()));
        }
    }
}
