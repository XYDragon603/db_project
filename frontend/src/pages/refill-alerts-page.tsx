import { useEffect, useState } from "react";
import { addRefill, getRefillAlerts } from "@/api/client";
import type { LoginResponse, RefillAlert } from "@/api/types";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { RefillAlertCard } from "@/components/shared/refill-alert-card";
import { UserLayout } from "@/layouts/user-layout";

export function RefillAlertsPage({ user }: { user: LoginResponse }) {
  const [alerts, setAlerts] = useState<RefillAlert[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmittingMedicationId, setIsSubmittingMedicationId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  async function loadAlerts() {
    setIsLoading(true);
    setError(null);
    setMessage(null);
    try {
      const next = await getRefillAlerts(user.userId);
      setAlerts(next);
    } catch {
      setError("Unable to load refill alerts.");
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadAlerts();
  }, [user.userId]);

  async function handleAddRefill(medicationId: number) {
    setIsSubmittingMedicationId(medicationId);
    setError(null);
    setMessage(null);
    try {
      const medicationName = alerts.find((item) => item.medicationId === medicationId)?.medicineName ?? "Medication";
      await addRefill(user.userId, {
        medicationId,
        quantityAdded: 30,
        note: "Demo quick refill",
      });
      await loadAlerts();
      setMessage(`Refill saved for ${medicationName}.`);
    } catch {
      setError("Unable to save that refill right now.");
    } finally {
      setIsSubmittingMedicationId(null);
    }
  }

  return (
    <UserLayout title="Refill Alerts">
      <PageHeader
        eyebrow="Refill Alerts"
        title="Low-stock medications"
        description="This screen collects refill risk into one calm but noticeable view."
      />
      {error ? <FeedbackMessage message={error} variant="error" /> : null}
      {message ? <div className="mt-4"><FeedbackMessage message={message} variant="success" /></div> : null}
      {isLoading ? (
        <LoadingState label="Loading refill alerts..." />
      ) : alerts.length ? (
        <div className="grid gap-4 lg:grid-cols-2">
          {alerts.map((alert) => (
            <RefillAlertCard
              key={alert.medicationId}
              medication={alert.medicineName}
              currentQuantity={alert.currentQuantity}
              refillThreshold={alert.refillThreshold}
              onAddRefill={() => handleAddRefill(alert.medicationId)}
              isSubmitting={isSubmittingMedicationId === alert.medicationId}
            />
          ))}
        </div>
      ) : (
        <EmptyState
          title="No refill alerts right now"
          description="Once a medication falls to or below its refill threshold, it will appear here."
        />
      )}
    </UserLayout>
  );
}
