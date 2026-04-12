package at.htl.leonding.vinylmaster.service;

import at.htl.leonding.vinylmaster.model.Vinyl;

import java.util.List;

public interface VinylService {
    Vinyl addVinyl(Vinyl vinyl) throws DuplicateVinylException, ValidationException;
    Vinyl updateVinyl(Vinyl vinyl) throws DuplicateVinylException, ValidationException;
    List<Vinyl> getAllVinyls();
    Vinyl getVinylById(Long id);
    void deleteVinyl(Long id);
}
