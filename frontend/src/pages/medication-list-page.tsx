import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deactivateMedication, getMedications } from "@/api/client";
import type { LoginResponse, Medication } from "@/api/types";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FilterBar } from "@/components/shared/filter-bar";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { SectionCard } from "@/components/shared/section-card";
import { Button } from "@/components/ui/button";
import { UserLayout } from "@/layouts/user-layout";

export function MedicationListPage({ user }: { user: LoginResponse }) {
  const [medications, setMedications] = useState<Medication[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDeletingId, setIsDeletingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadMedications() {
      setIsLoading(true);
      setError(null);
      setMessage(null);
      try {
        const next = await getMedications(user.userId);
        if (isMounted) {
          setMedications(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load medications.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadMedications();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  async function handleDeactivateMedication(medication: Medication) {
    const confirmed = window.confirm(`Remove ${medication.medicineName} from your active medication list?`);
    if (!confirmed) {
      return;
    }

    setIsDeletingId(medication.medicationId);
    setError(null);
    setMessage(null);

    try {
      await deactivateMedication(user.userId, medication.medicationId);
      setMedications((current) => current.filter((item) => item.medicationId !== medication.medicationId));
      setMessage(`${medication.medicineName} was removed from your active medication list.`);
    } catch (nextError) {
      if (nextError instanceof Error && nextError.message.includes("403")) {
        setError("You can only remove medications from your own account.");
      } else {
        setError("Unable to remove this medication right now.");
      }
    } finally {
      setIsDeletingId(null);
    }
  }

  return (
    <UserLayout title="Medication List">
      <PageHeader
        eyebrow="Medication List"
        title="All medications"
        description="Review stock, thresholds, and the medication records used in the schedule flow."
        actions={(
          <Link to="/user/medications/new">
            <Button>Add medication</Button>
          </Link>
        )}
      />
      <FilterBar placeholder="Search medications..." />
      {message ? <FeedbackMessage message={message} variant="success" /> : null}
      {error ? <FeedbackMessage message={error} variant="error" /> : null}
      {isLoading ? (
        <LoadingState label="Loading medication records..." />
      ) : medications.length ? (
        <div className="grid gap-4 lg:grid-cols-2">
          {medications.map((medication) => (
            <SectionCard
              key={medication.medicationId}
              title={`${medication.medicineName} ${medication.dosage}`}
            >
              <p>Form: {medication.form}</p>
              <p>Current quantity: {medication.currentQuantity}</p>
              <p>Refill threshold: {medication.refillThreshold}</p>
              <p>Status: {medication.active ? "Active" : "Inactive"}</p>
              <div className="flex flex-wrap gap-3 pt-2">
                <Link to={`/user/medications/${medication.medicationId}/edit`}>
                  <Button variant="secondary">Edit medication</Button>
                </Link>
                <Button
                  variant="danger"
                  disabled={isDeletingId === medication.medicationId}
                  onClick={() => void handleDeactivateMedication(medication)}
                >
                  {isDeletingId === medication.medicationId ? "Removing..." : "Delete"}
                </Button>
              </div>
            </SectionCard>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No medications added yet"
          description="Create your first medication record to start the dashboard, schedule, and refill flows."
          action={(
            <Link to="/user/medications/new">
              <Button>Add medication</Button>
            </Link>
          )}
        />
      )}
    </UserLayout>
  );
}
