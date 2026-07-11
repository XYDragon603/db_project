package com.medminder.service.medication;

import com.medminder.domain.repository.CountryRepository;
import com.medminder.domain.repository.MedicationCatalogRepository;
import com.medminder.web.dto.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicationCatalogService {
    private final CountryRepository countryRepository;
    private final MedicationCatalogRepository catalogRepository;

    public MedicationCatalogService(CountryRepository countryRepository, MedicationCatalogRepository catalogRepository) {
        this.countryRepository = countryRepository;
        this.catalogRepository = catalogRepository;
    }

    @Transactional(readOnly = true)
    public List<CountryResponse> getCountries() {
        return countryRepository.findByActiveTrueOrderByCountryNameAsc().stream()
            .map(c -> new CountryResponse(c.getCountryCode(), c.getCountryName(), c.getRegulatoryAuthority()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicationCatalogResponse> getCatalog(String countryCode) {
        return catalogRepository
            .findDistinctByCountryCountryCodeAndCatalogStatusAndBrandsActiveTrueOrderByGenericNameAsc(
                countryCode.toUpperCase(), "ACTIVE"
            ).stream()
            .map(item -> new MedicationCatalogResponse(
                item.getCatalogId(), item.getCountry().getCountryCode(), item.getGenericName(),
                item.getDosageForm(), item.getStrength(), item.isPrescriptionRequired(),
                item.getBrands().stream().filter(b -> b.isActive())
                    .map(b -> new MedicationBrandResponse(
                        b.getBrandId(), b.getBrandName(), b.getManufacturer(), b.getLocalRegistrationCode()
                    )).toList()
            )).toList();
    }
}
