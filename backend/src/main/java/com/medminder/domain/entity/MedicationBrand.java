package com.medminder.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medication_brands")
public class MedicationBrand {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long brandId;
    @ManyToOne(optional = false) @JoinColumn(name = "catalog_id", nullable = false)
    private MedicationCatalog catalog;
    @Column(name = "brand_name", nullable = false, length = 150)
    private String brandName;
    @Column(length = 150)
    private String manufacturer;
    @Column(name = "local_registration_code", length = 100)
    private String localRegistrationCode;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public MedicationCatalog getCatalog() { return catalog; }
    public void setCatalog(MedicationCatalog catalog) { this.catalog = catalog; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getLocalRegistrationCode() { return localRegistrationCode; }
    public void setLocalRegistrationCode(String localRegistrationCode) { this.localRegistrationCode = localRegistrationCode; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
