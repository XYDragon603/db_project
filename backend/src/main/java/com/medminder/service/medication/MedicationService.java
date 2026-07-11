package com.medminder.service.medication;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.Medication;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.MedicationCatalogRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.dto.CreateMedicationRequest;
import com.medminder.web.dto.MedicationResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final MedicationCatalogRepository catalogRepository;

    public MedicationService(
        MedicationRepository medicationRepository,
        UserRepository userRepository,
        AuditLogRepository auditLogRepository,
        MedicationCatalogRepository catalogRepository
    ) {
        this.medicationRepository = medicationRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.catalogRepository = catalogRepository;
    }

    public List<MedicationResponse> getMedications(Long userId) {
        return medicationRepository.findByUserUserIdAndActiveTrueOrderByMedicineNameAsc(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    public MedicationResponse getMedication(Long userId, Long medicationId) {
        return toResponse(requireOwnedMedication(userId, medicationId));
    }

    @Transactional
    public MedicationResponse createMedication(Long userId, CreateMedicationRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var medication = new Medication();
        medication.setUser(user);
        if (request.catalogId() != null) {
            medication.setCatalog(catalogRepository.findById(request.catalogId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catalog item not found")));
        }
        medication.setMedicineName(request.medicineName());
        medication.setDosage(request.dosage());
        medication.setForm(request.form());
        medication.setCurrentQuantity(request.currentQuantity());
        medication.setRefillThreshold(request.refillThreshold());
        medication.setNotes(request.notes());
        medication.setStartDate(parseDate(request.startDate()));
        medication.setEndDate(parseDate(request.endDate()));
        var saved = medicationRepository.save(medication);

        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("ADD_MEDICATION");
        auditLog.setTargetTable("medications");
        auditLog.setTargetId(saved.getMedicationId());
        auditLog.setDetails("Created medication " + saved.getMedicineName());
        auditLogRepository.save(auditLog);

        return toResponse(saved);
    }

    @Transactional
    public MedicationResponse updateMedication(Long userId, Long medicationId, CreateMedicationRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        var medication = requireOwnedMedication(userId, medicationId);

        medication.setMedicineName(request.medicineName());
        medication.setDosage(request.dosage());
        medication.setForm(request.form());
        medication.setCurrentQuantity(request.currentQuantity());
        medication.setRefillThreshold(request.refillThreshold());
        medication.setNotes(request.notes());
        medication.setStartDate(parseDate(request.startDate()));
        medication.setEndDate(parseDate(request.endDate()));
        var saved = medicationRepository.save(medication);

        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("UPDATE_MEDICATION");
        auditLog.setTargetTable("medications");
        auditLog.setTargetId(saved.getMedicationId());
        auditLog.setDetails("Updated medication " + saved.getMedicineName());
        auditLogRepository.save(auditLog);

        return toResponse(saved);
    }

    @Transactional
    public MedicationResponse deactivateMedication(Long userId, Long medicationId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        var medication = requireOwnedMedication(userId, medicationId);

        medication.setActive(false);
        var saved = medicationRepository.save(medication);

        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("DEACTIVATE_MEDICATION");
        auditLog.setTargetTable("medications");
        auditLog.setTargetId(saved.getMedicationId());
        auditLog.setDetails("Deactivated medication " + saved.getMedicineName());
        auditLogRepository.save(auditLog);

        return toResponse(saved);
    }

    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private Medication requireOwnedMedication(Long userId, Long medicationId) {
        var medication = medicationRepository.findById(medicationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medication not found"));

        if (!medication.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own medication");
        }

        return medication;
    }

    private MedicationResponse toResponse(Medication medication) {
        return new MedicationResponse(
            medication.getMedicationId(),
            medication.getMedicineName(),
            medication.getDosage(),
            medication.getForm(),
            medication.getCurrentQuantity(),
            medication.getRefillThreshold(),
            medication.isActive(),
            medication.getStartDate() == null ? null : medication.getStartDate().toString(),
            medication.getEndDate() == null ? null : medication.getEndDate().toString(),
            medication.getNotes()
        );
    }
}
