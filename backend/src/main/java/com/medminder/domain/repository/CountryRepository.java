package com.medminder.domain.repository;

import com.medminder.domain.entity.Country;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
    List<Country> findByActiveTrueOrderByCountryNameAsc();
}
