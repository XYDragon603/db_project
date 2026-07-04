import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getMedicationById, updateMedication } from "@/api/client";
import type { LoginResponse, Medication } from "@/api/types";
import { DateInput } from "@/components/shared/date-input";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { FormSelect } from "@/components/shared/form-select";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { SectionCard } from "@/components/shared/section-card";
import { Button } from "@/components/ui/button";
import { UserLayout } from "@/layouts/user-layout";

const FORM_OPTIONS = ["Tablet", "Capsule", "Liquid"];

export function EditMedicationPage({ user }: { user: LoginResponse }) {
  const { medicationId } = useParams();
  const [medication, setMedication] = useState<Medication | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadMedication() {
      if (!medicationId) {
        setError("Medication ID is missing.");
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setError(null);
      setMessage(null);
      try {
        const next = await getMedicationById(user.userId, Number(medicationId));
        if (isMounted) {
          setMedication(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load this medication.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadMedication();

    return () => {
      isMounted = false;
    };
  }, [medicationId, user.userId]);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!medicationId) {
      setError("Medication ID is missing.");
      return;
    }

    const form = new FormData(event.currentTarget);
    const medicineName = String(form.get("medicineName") ?? "Medication");

    setIsSaving(true);
    setError(null);
    setMessage(null);
    try {
      const updated = await updateMedication(user.userId, Number(medicationId), Object.fromEntries(form.entries()));
      setMedication(updated);
      setMessage(`${medicineName} was updated successfully.`);
    } catch {
      setError("Unable to update this medication right now.");
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <UserLayout title="Edit Medication">
      <PageHeader
        eyebrow="Edit Medication"
        title="Update a medication record"
        description="Edit the core medication fields without changing schedules, dose logs, or refill records."
      />
      {isLoading ? (
        <LoadingState label="Loading medication details..." />
      ) : medication ? (
        <SectionCard title={`${medication.medicineName} details`}>
          <form className="grid gap-4 md:grid-cols-2" onSubmit={handleSubmit}>
            <FormInput
              label="Medicine name"
              name="medicineName"
              placeholder="Metformin"
              defaultValue={medication.medicineName}
              required
            />
            <FormInput
              label="Dosage"
              name="dosage"
              placeholder="500mg"
              defaultValue={medication.dosage}
              required
            />
            <FormSelect
              label="Form"
              name="form"
              options={FORM_OPTIONS}
              defaultValue={medication.form}
              required
            />
            <FormInput
              label="Current quantity"
              name="currentQuantity"
              placeholder="30"
              type="number"
              defaultValue={medication.currentQuantity}
              min={0}
              required
            />
            <FormInput
              label="Refill threshold"
              name="refillThreshold"
              placeholder="5"
              type="number"
              defaultValue={medication.refillThreshold}
              min={0}
              required
            />
            <DateInput label="Start date" name="startDate" defaultValue={medication.startDate ?? ""} />
            <DateInput label="End date" name="endDate" defaultValue={medication.endDate ?? ""} />
            <div className="md:col-span-2">
              <FormInput
                label="Notes"
                name="notes"
                placeholder="Take after lunch"
                defaultValue={medication.notes ?? ""}
              />
            </div>
            <div className="md:col-span-2 flex flex-wrap items-center gap-3">
              <Button type="submit" disabled={isSaving}>
                {isSaving ? "Saving..." : "Save changes"}
              </Button>
              <Link to="/user/medications">
                <Button variant="secondary">Back to list</Button>
              </Link>
            </div>
            <div className="md:col-span-2 space-y-3">
              {message ? <FeedbackMessage message={message} variant="success" /> : null}
              {error ? <FeedbackMessage message={error} variant="error" /> : null}
            </div>
          </form>
        </SectionCard>
      ) : (
        <SectionCard title="Medication unavailable">
          <div className="space-y-4">
            {error ? <FeedbackMessage message={error} variant="error" /> : null}
            <Link to="/user/medications">
              <Button variant="secondary">Back to list</Button>
            </Link>
          </div>
        </SectionCard>
      )}
    </UserLayout>
  );
}
