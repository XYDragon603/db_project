package com.medminder.domain.repository;

import com.medminder.domain.entity.MedicationCatalog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationCatalogRepository extends JpaRepository<MedicationCatalog, Long> {
    List<MedicationCatalog> findDistinctByCountryCountryCodeAndCatalogStatusAndBrandsActiveTrueOrderByGenericNameAsc(
        String countryCode, String catalogStatus
    );
}
