package com.medminder.web.medication;

import com.medminder.service.medication.MedicationCatalogService;
import com.medminder.web.dto.CountryResponse;
import com.medminder.web.dto.MedicationCatalogResponse;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medication-catalog")
public class MedicationCatalogController {
    private final MedicationCatalogService catalogService;

    public MedicationCatalogController(MedicationCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/countries")
    public List<CountryResponse> getCountries() {
        return catalogService.getCountries();
    }

    @GetMapping
    public List<MedicationCatalogResponse> getCatalog(@RequestParam String countryCode) {
        return catalogService.getCatalog(countryCode);
    }
}
