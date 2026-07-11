package com.medminder.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @Column(name = "country_code", length = 2)
    private String countryCode;
    @Column(name = "country_name", nullable = false, unique = true, length = 100)
    private String countryName;
    @Column(name = "regulatory_authority", nullable = false, length = 150)
    private String regulatoryAuthority;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getRegulatoryAuthority() { return regulatoryAuthority; }
    public void setRegulatoryAuthority(String regulatoryAuthority) { this.regulatoryAuthority = regulatoryAuthority; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
