import { useState } from "react";
import { Link } from "react-router-dom";
import { createMedication } from "@/api/client";
import type { LoginResponse } from "@/api/types";
import { DateInput } from "@/components/shared/date-input";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { FormSelect } from "@/components/shared/form-select";
import { PageHeader } from "@/components/shared/page-header";
import { Button } from "@/components/ui/button";
import { SectionCard } from "@/components/shared/section-card";
import { UserLayout } from "@/layouts/user-layout";

function resolveMedicationSaveError(error: unknown) {
  if (error instanceof Error && error.message.includes("400")) {
    return "Complete the required medication fields before saving.";
  }
  if (error instanceof Error && error.message.includes("403")) {
    return "You can only create medications for your own account.";
  }

  return "Unable to save this medication right now.";
}

export function AddMedicationPage({ user }: { user: LoginResponse }) {
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const formElement = event.currentTarget;
    const form = new FormData(formElement);
    const medicineName = String(form.get("medicineName") ?? "Medication");

    setIsSaving(true);
    setMessage(null);
    setError(null);
    try {
      await createMedication(user.userId, Object.fromEntries(form.entries()));
      setError(null);
      setMessage(`${medicineName} was added successfully. You can now add a daily schedule.`);
      formElement.reset();
    } catch (nextError) {
      setError(resolveMedicationSaveError(nextError));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <UserLayout title="Add Medication">
      <PageHeader
        eyebrow="Add Medication"
        title="Create a medication record"
        description="This form stays focused on the core fields needed for the demo-critical flow."
      />
      <SectionCard title="Medication details">
        <form className="grid gap-4 md:grid-cols-2" onSubmit={handleSubmit}>
          <FormInput label="Medicine name" name="medicineName" placeholder="Metformin" required />
          <FormInput label="Dosage" name="dosage" placeholder="500mg" required />
          <FormSelect label="Form" name="form" options={["Tablet", "Capsule", "Liquid"]} required />
          <FormInput label="Current quantity" name="currentQuantity" placeholder="30" type="number" min="0" defaultValue="30" required />
          <FormInput label="Refill threshold" name="refillThreshold" placeholder="5" type="number" min="0" defaultValue="5" required />
          <DateInput label="Start date" name="startDate" />
          <div className="md:col-span-2">
            <FormInput label="Notes" name="notes" placeholder="Take after lunch" />
          </div>
          <div className="md:col-span-2 flex flex-wrap items-center gap-3">
            <Button type="submit" disabled={isSaving}>
              {isSaving ? "Saving..." : "Save medication"}
            </Button>
            <Link to="/user/schedules">
              <Button type="button" variant="secondary">Go to schedules</Button>
            </Link>
          </div>
          <div className="md:col-span-2">
            {message ? <FeedbackMessage message={message} variant="success" /> : null}
            {error ? <FeedbackMessage message={error} variant="error" /> : null}
          </div>
        </form>
      </SectionCard>
    </UserLayout>
  );
}
