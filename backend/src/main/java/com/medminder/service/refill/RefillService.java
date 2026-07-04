package com.medminder.service.refill;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.RefillRecord;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.RefillRecordRepository;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.dto.CreateRefillRequest;
import com.medminder.web.dto.RefillAlertResponse;
import com.medminder.web.dto.RefillRecordResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefillService {

    private final MedicationRepository medicationRepository;
    private final RefillRecordRepository refillRecordRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public RefillService(
        MedicationRepository medicationRepository,
        RefillRecordRepository refillRecordRepository,
        UserRepository userRepository,
        AuditLogRepository auditLogRepository
    ) {
        this.medicationRepository = medicationRepository;
        this.refillRecordRepository = refillRecordRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<RefillAlertResponse> getAlerts(Long userId) {
        return medicationRepository.findByUserUserIdAndActiveTrueOrderByMedicineNameAsc(userId).stream()
            .filter(medication -> medication.getCurrentQuantity() <= medication.getRefillThreshold())
            .map(medication -> new RefillAlertResponse(
                medication.getMedicationId(),
                medication.getMedicineName(),
                medication.getCurrentQuantity(),
                medication.getRefillThreshold()
            ))
            .toList();
    }

    @Transactional
    public RefillRecordResponse addRefill(Long userId, CreateRefillRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var medication = medicationRepository.findById(request.medicationId())
            .orElseThrow(() -> new IllegalArgumentException("Medication not found"));

        if (!medication.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Medication does not belong to the user");
        }

        var refillRecord = new RefillRecord();
        refillRecord.setUser(user);
        refillRecord.setMedication(medication);
        refillRecord.setRefillDate(LocalDate.now());
        refillRecord.setQuantityAdded(request.quantityAdded());
        refillRecord.setNote(request.note());
        var saved = refillRecordRepository.save(refillRecord);

        medication.setCurrentQuantity(medication.getCurrentQuantity() + request.quantityAdded());

        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("ADD_REFILL");
        auditLog.setTargetTable("refill_records");
        auditLog.setTargetId(saved.getRefillId());
        auditLog.setDetails("Added " + request.quantityAdded() + " units to medication " + medication.getMedicationId());
        auditLogRepository.save(auditLog);

        return new RefillRecordResponse(
            saved.getRefillId(),
            medication.getMedicationId(),
            saved.getQuantityAdded(),
            medication.getCurrentQuantity()
        );
    }
}
