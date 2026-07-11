package com.medminder.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medication_catalog")
public class MedicationCatalog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catalog_id")
    private Long catalogId;
    @ManyToOne(optional = false) @JoinColumn(name = "country_code", nullable = false)
    private Country country;
    @Column(name = "generic_name", nullable = false, length = 150)
    private String genericName;
    @Column(name = "dosage_form", nullable = false, length = 50)
    private String dosageForm;
    @Column(nullable = false, length = 100)
    private String strength;
    @Column(name = "prescription_required", nullable = false)
    private boolean prescriptionRequired;
    @Column(name = "catalog_status", nullable = false, length = 20)
    private String catalogStatus = "ACTIVE";
    @OneToMany(mappedBy = "catalog")
    private List<MedicationBrand> brands = new ArrayList<>();

    public Long getCatalogId() { return catalogId; }
    public void setCatalogId(Long catalogId) { this.catalogId = catalogId; }
    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }
    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }
    public String getCatalogStatus() { return catalogStatus; }
    public void setCatalogStatus(String catalogStatus) { this.catalogStatus = catalogStatus; }
    public List<MedicationBrand> getBrands() { return brands; }
    public void setBrands(List<MedicationBrand> brands) { this.brands = brands; }
}
