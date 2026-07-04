import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { createSchedule, deactivateSchedule, getMedications, getSchedules } from "@/api/client";
import type { LoginResponse, Medication, Schedule } from "@/api/types";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { ScheduleCard } from "@/components/shared/schedule-card";
import { SectionCard } from "@/components/shared/section-card";
import { Button } from "@/components/ui/button";
import {
  formatDoseAmountLabel,
  getScheduleMeta,
} from "@/features/medications/display-utils";
import { UserLayout } from "@/layouts/user-layout";

export function ScheduleManagementPage({ user }: { user: LoginResponse }) {
  const [medications, setMedications] = useState<Medication[]>([]);
  const [selectedMedicationId, setSelectedMedicationId] = useState<number | null>(null);
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeletingScheduleId, setIsDeletingScheduleId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadMedicationsOnly() {
      setIsLoading(true);
      setError(null);
      setMessage(null);
      try {
        const medicationResults = await getMedications(user.userId);
        if (!isMounted) {
          return;
        }
        setMedications(medicationResults);

        const hasSelectedMedication = selectedMedicationId !== null
          && medicationResults.some((medication) => medication.medicationId === selectedMedicationId);
        const nextSelectedMedicationId = hasSelectedMedication
          ? selectedMedicationId
          : medicationResults[0]?.medicationId ?? null;
        setSelectedMedicationId(nextSelectedMedicationId);
        if (medicationResults.length === 0) {
          setSchedules([]);
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

    void loadMedicationsOnly();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  useEffect(() => {
    if (!selectedMedicationId) {
      setSchedules([]);
      return;
    }
    const medicationId = selectedMedicationId;

    let isMounted = true;

    async function loadSchedulesForMedication() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getSchedules(user.userId, medicationId);
        if (isMounted) {
          setSchedules(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load daily schedules.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadSchedulesForMedication();

    return () => {
      isMounted = false;
    };
  }, [selectedMedicationId, user.userId]);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedMedicationId) {
      setError("Select a medication before creating a schedule.");
      return;
    }

    const formElement = event.currentTarget;
    const form = new FormData(formElement);

    setIsSaving(true);
    setError(null);
    setMessage(null);
    try {
      const next = await createSchedule(user.userId, {
        medicationId: selectedMedicationId,
        scheduledTime: form.get("scheduledTime"),
        doseAmount: form.get("doseAmount"),
        frequency: form.get("frequency"),
      });
      setSchedules((current) => [...current, next]);
      const medicationLabel = medications.find((item) => item.medicationId === selectedMedicationId)?.medicineName ?? "Schedule";
      setError(null);
      setMessage(`Daily schedule added for ${medicationLabel}.`);
      formElement.reset();
    } catch {
      setError("Unable to save this schedule.");
    } finally {
      setIsSaving(false);
    }
  }

  async function handleDeactivateSchedule(schedule: Schedule) {
    const confirmed = window.confirm(`Remove the ${schedule.scheduledTime} daily reminder from your active schedule list?`);
    if (!confirmed) {
      return;
    }

    setIsDeletingScheduleId(schedule.scheduleId);
    setError(null);
    setMessage(null);

    try {
      await deactivateSchedule(user.userId, schedule.scheduleId);
      setSchedules((current) => current.filter((item) => item.scheduleId !== schedule.scheduleId));
      setMessage(`The ${schedule.scheduledTime} reminder was removed from your active schedule list.`);
    } catch (nextError) {
      if (nextError instanceof Error && nextError.message.includes("404")) {
        setError("Schedule delete endpoint is not available yet. Restart the backend and try again.");
      } else
      if (nextError instanceof Error && nextError.message.includes("403")) {
        setError("You can only remove schedules from your own account.");
      } else {
        setError("Unable to remove this schedule right now.");
      }
    } finally {
      setIsDeletingScheduleId(null);
    }
  }

  return (
    <UserLayout title="Schedule Management">
      <PageHeader
        eyebrow="Schedule Management"
        title="Medication schedule setup"
        description="The first version only supports daily schedules, which is enough for the demo-critical flow."
      />
      {error ? <div className="mb-5"><FeedbackMessage message={error} variant="error" /></div> : null}
      {message ? <div className="mb-5"><FeedbackMessage message={message} variant="success" /></div> : null}
      <div className="grid gap-5 xl:grid-cols-[1.4fr_1fr]">
        <SectionCard title="Current schedules" description="Schedules are loaded from the selected medication record.">
          <label className="space-y-2">
            <span className="text-sm font-medium text-slate-700">Medication</span>
            <select
              className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm"
              value={selectedMedicationId ?? ""}
              onChange={(event) => {
                setMessage(null);
                setError(null);
                setSelectedMedicationId(Number(event.target.value));
              }}
            >
              {medications.length === 0 ? <option value="">No medications available</option> : null}
              {medications.map((medication) => (
                <option key={medication.medicationId} value={medication.medicationId}>
                  {medication.medicineName} {medication.dosage}
                </option>
              ))}
            </select>
          </label>
          {isLoading ? (
            <LoadingState label="Loading saved schedules..." />
          ) : medications.length === 0 ? (
            <EmptyState
              title="Add a medication before creating a schedule"
              description="Schedules are created from an existing medication record, so the first step for a new account is adding at least one medication."
              action={(
                <Link to="/user/medications/new">
                  <Button>Add medication</Button>
                </Link>
              )}
            />
          ) : schedules.length ? (
            <div className="grid gap-3">
              {schedules.map((schedule) => (
                <ScheduleCard
                  key={schedule.scheduleId}
                  time={schedule.scheduledTime}
                  meta={getScheduleMeta(
                    schedule.frequency,
                    formatDoseAmountLabel(schedule.doseAmount),
                  )}
                  active={schedule.active}
                  action={{
                    label: isDeletingScheduleId === schedule.scheduleId ? "Removing..." : "Delete",
                    onClick: () => void handleDeactivateSchedule(schedule),
                    disabled: isDeletingScheduleId === schedule.scheduleId,
                  }}
                />
              ))}
            </div>
          ) : (
            <EmptyState
              title="No schedules created yet"
              description="Add at least one daily reminder time so today's medication cards can appear on the dashboard."
              action={<Button onClick={() => document.querySelector<HTMLInputElement>('input[name=\"scheduledTime\"]')?.focus()}>Create first schedule</Button>}
            />
          )}
        </SectionCard>
        <SectionCard title="Add daily schedule">
          {medications.length === 0 ? (
            <EmptyState
              title="No medication selected yet"
              description="Create a medication first, then return here to add a daily reminder schedule."
              action={(
                <Link to="/user/medications/new">
                  <Button>Add medication</Button>
                </Link>
              )}
            />
          ) : (
            <form className="space-y-4" onSubmit={handleSubmit}>
              <label className="space-y-2">
                <span className="text-sm font-medium text-slate-700">Scheduled time</span>
                <input
                  className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm"
                  name="scheduledTime"
                  type="time"
                  defaultValue="08:00"
                  required
                />
              </label>
              <FormInput label="Dose amount" name="doseAmount" placeholder="1" type="number" min="0.01" step="0.01" defaultValue="1" required />
              <label className="space-y-2">
                <span className="text-sm font-medium text-slate-700">Frequency</span>
                <select
                  className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm"
                  name="frequency"
                  defaultValue="DAILY"
                >
                  <option value="DAILY">DAILY</option>
                </select>
              </label>
              <Button type="submit" className="w-full" disabled={isSaving || !selectedMedicationId}>
                {isSaving ? "Saving..." : "Save schedule"}
              </Button>
            </form>
          )}
        </SectionCard>
      </div>
    </UserLayout>
  );
}
