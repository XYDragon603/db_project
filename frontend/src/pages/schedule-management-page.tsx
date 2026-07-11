import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { createSchedules, deactivateSchedule, getMedications, getSchedules, updateSchedule } from "@/api/client";
import type { LoginResponse, Medication, Schedule } from "@/api/types";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { ScheduleCard } from "@/components/shared/schedule-card";
import { SectionCard } from "@/components/shared/section-card";
import { Button } from "@/components/ui/button";
import { Dialog } from "@/components/ui/dialog";
import {
  formatDoseAmountLabel,
  getScheduleMeta,
} from "@/features/medications/display-utils";
import { UserLayout } from "@/layouts/user-layout";

type TimePreset = "MORNING" | "NOON" | "EVENING" | "BEDTIME" | "CUSTOM";

interface ReminderTime {
  preset: TimePreset;
  time: string;
}

const TIME_PRESETS: Array<{ value: TimePreset; label: string; time?: string }> = [
  { value: "MORNING", label: "Morning", time: "08:00" },
  { value: "NOON", label: "Noon", time: "12:00" },
  { value: "EVENING", label: "Evening", time: "18:00" },
  { value: "BEDTIME", label: "Before bed", time: "22:00" },
  { value: "CUSTOM", label: "Custom time" },
];

const DEFAULT_REMINDER_TIMES: ReminderTime[] = [
  { preset: "MORNING", time: "08:00" },
  { preset: "NOON", time: "12:00" },
  { preset: "EVENING", time: "18:00" },
  { preset: "BEDTIME", time: "22:00" },
];

export function ScheduleManagementPage({ user }: { user: LoginResponse }) {
  const [medications, setMedications] = useState<Medication[]>([]);
  const [selectedMedicationId, setSelectedMedicationId] = useState<number | null>(null);
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeletingScheduleId, setIsDeletingScheduleId] = useState<number | null>(null);
  const [editingSchedule, setEditingSchedule] = useState<Schedule | null>(null);
  const [editTime, setEditTime] = useState("");
  const [editDoseAmount, setEditDoseAmount] = useState("");
  const [isUpdating, setIsUpdating] = useState(false);
  const [dailyDoseCount, setDailyDoseCount] = useState(1);
  const [reminderTimes, setReminderTimes] = useState<ReminderTime[]>([DEFAULT_REMINDER_TIMES[0]]);
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
    const uniqueTimes = new Set(reminderTimes.map((reminder) => reminder.time));
    if (uniqueTimes.size !== reminderTimes.length) {
      setError("Choose a different time for each daily dose.");
      return;
    }
    const existingTimes = new Set(schedules.filter((schedule) => schedule.active).map((schedule) => schedule.scheduledTime));
    const duplicateExistingTime = reminderTimes.find((reminder) => existingTimes.has(reminder.time));
    if (duplicateExistingTime) {
      setError(`An active reminder already exists at ${duplicateExistingTime.time}.`);
      return;
    }

    setIsSaving(true);
    setError(null);
    setMessage(null);
    try {
      const next = await createSchedules(user.userId, reminderTimes.map((reminder) => ({
        medicationId: selectedMedicationId,
        scheduledTime: reminder.time,
        doseAmount: form.get("doseAmount"),
        frequency: "DAILY",
      })));
      setSchedules((current) => [...current, ...next].sort((a, b) => a.scheduledTime.localeCompare(b.scheduledTime)));
      const medicationLabel = medications.find((item) => item.medicationId === selectedMedicationId)?.medicineName ?? "Schedule";
      setError(null);
      setMessage(`${next.length} daily ${next.length === 1 ? "reminder" : "reminders"} added for ${medicationLabel}.`);
      formElement.reset();
      setDailyDoseCount(1);
      setReminderTimes([DEFAULT_REMINDER_TIMES[0]]);
    } catch {
      setError("Unable to save this schedule.");
    } finally {
      setIsSaving(false);
    }
  }

  function handleDailyDoseCountChange(nextCount: number) {
    setDailyDoseCount(nextCount);
    setReminderTimes((current) => Array.from(
      { length: nextCount },
      (_, index) => current[index] ?? DEFAULT_REMINDER_TIMES[index],
    ));
    setError(null);
    setMessage(null);
  }

  function handlePresetChange(index: number, preset: TimePreset) {
    const presetTime = TIME_PRESETS.find((item) => item.value === preset)?.time;
    setReminderTimes((current) => current.map((reminder, reminderIndex) => (
      reminderIndex === index
        ? { preset, time: presetTime ?? reminder.time }
        : reminder
    )));
  }

  function handleTimeChange(index: number, time: string) {
    const matchedPreset = TIME_PRESETS.find((item) => item.time === time)?.value ?? "CUSTOM";
    setReminderTimes((current) => current.map((reminder, reminderIndex) => (
      reminderIndex === index ? { preset: matchedPreset, time } : reminder
    )));
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

  function openEditDialog(schedule: Schedule) {
    setEditingSchedule(schedule);
    setEditTime(schedule.scheduledTime);
    setEditDoseAmount(schedule.doseAmount);
    setError(null);
    setMessage(null);
  }

  function closeEditDialog() {
    if (!isUpdating) {
      setEditingSchedule(null);
    }
  }

  async function handleUpdateSchedule(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!editingSchedule) {
      return;
    }
    if (!editTime) {
      setError("Choose an exact reminder time.");
      return;
    }
    if (!editDoseAmount || Number(editDoseAmount) <= 0) {
      setError("Dose amount must be greater than zero.");
      return;
    }
    const duplicate = schedules.some((schedule) => (
      schedule.active
      && schedule.scheduleId !== editingSchedule.scheduleId
      && schedule.scheduledTime === editTime
    ));
    if (duplicate) {
      setError(`An active reminder already exists at ${editTime}.`);
      return;
    }

    setIsUpdating(true);
    setError(null);
    setMessage(null);
    try {
      const updated = await updateSchedule(user.userId, editingSchedule.scheduleId, {
        scheduledTime: editTime,
        doseAmount: editDoseAmount,
      });
      setSchedules((current) => current
        .map((schedule) => schedule.scheduleId === updated.scheduleId ? updated : schedule)
        .sort((a, b) => a.scheduledTime.localeCompare(b.scheduledTime)));
      setEditingSchedule(null);
      setMessage(`Daily reminder updated to ${updated.scheduledTime}.`);
    } catch (nextError) {
      if (nextError instanceof Error && nextError.message.includes("403")) {
        setError("You can only update schedules from your own account.");
      } else {
        setError("Unable to update this schedule right now.");
      }
    } finally {
      setIsUpdating(false);
    }
  }

  return (
    <UserLayout title="Schedule Management">
      <PageHeader
        eyebrow="Schedule Management"
        title="Medication schedule setup"
        description="Choose how many times a medication is taken each day, then use a suggested time of day or set an exact reminder time."
        actions={(
          <Link to="/user/medications/new">
            <Button>Add medication</Button>
          </Link>
        )}
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
                  secondaryAction={{
                    label: "Edit",
                    onClick: () => openEditDialog(schedule),
                    disabled: isDeletingScheduleId !== null,
                  }}
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
              action={<Button onClick={() => document.querySelector<HTMLInputElement>('input[name="scheduledTime-0"]')?.focus()}>Create first schedule</Button>}
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
            <form className="space-y-5" onSubmit={handleSubmit}>
              <label className="space-y-2">
                <span className="text-sm font-medium text-slate-700">Times per day</span>
                <select
                  className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm"
                  value={dailyDoseCount}
                  onChange={(event) => handleDailyDoseCountChange(Number(event.target.value))}
                >
                  <option value={1}>Once daily</option>
                  <option value={2}>Twice daily</option>
                  <option value={3}>Three times daily</option>
                  <option value={4}>Four times daily</option>
                </select>
              </label>
              <div className="space-y-3">
                <div>
                  <p className="text-sm font-medium text-slate-700">Reminder times</p>
                  <p className="mt-1 text-xs text-slate-500">Suggested periods fill an exact time that you can adjust.</p>
                </div>
                {reminderTimes.map((reminder, index) => (
                  <div className="grid gap-2 rounded-2xl border border-slate-200 bg-slate-50/70 p-3 sm:grid-cols-[1fr_8.5rem]" key={`reminder-${index}`}>
                    <label className="space-y-1.5">
                      <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Dose {index + 1}</span>
                      <select
                        aria-label={`Dose ${index + 1} time of day`}
                        className="flex h-11 w-full rounded-xl border border-border bg-white px-3 text-sm"
                        value={reminder.preset}
                        onChange={(event) => handlePresetChange(index, event.target.value as TimePreset)}
                      >
                        {TIME_PRESETS.map((preset) => (
                          <option key={preset.value} value={preset.value}>{preset.label}</option>
                        ))}
                      </select>
                    </label>
                    <label className="space-y-1.5">
                      <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Exact time</span>
                      <input
                        aria-label={`Dose ${index + 1} exact time`}
                        className="flex h-11 w-full rounded-xl border border-border bg-white px-3 text-sm"
                        name={`scheduledTime-${index}`}
                        type="time"
                        value={reminder.time}
                        onChange={(event) => handleTimeChange(index, event.target.value)}
                        required
                      />
                    </label>
                  </div>
                ))}
              </div>
              <FormInput label="Dose amount" name="doseAmount" placeholder="1" type="number" min="0.01" step="0.01" defaultValue="1" required />
              <div className="flex items-center justify-between rounded-2xl bg-blue-50 px-4 py-3 text-sm">
                <span className="font-medium text-slate-700">Repeats</span>
                <span className="font-semibold text-primary">Every day</span>
              </div>
              <Button type="submit" className="w-full" disabled={isSaving || !selectedMedicationId}>
                {isSaving ? "Saving..." : `Save ${dailyDoseCount} ${dailyDoseCount === 1 ? "reminder" : "reminders"}`}
              </Button>
            </form>
          )}
        </SectionCard>
      </div>
      <Dialog
        open={editingSchedule !== null}
        title="Edit daily reminder"
        description="Adjust the exact reminder time or dose amount. The medication and daily frequency stay unchanged."
        onClose={closeEditDialog}
      >
        <form className="space-y-5" onSubmit={handleUpdateSchedule}>
          <label className="space-y-2">
            <span className="text-sm font-medium text-slate-700">Exact reminder time</span>
            <input
              className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm"
              type="time"
              value={editTime}
              onChange={(event) => setEditTime(event.target.value)}
              required
            />
          </label>
          <FormInput
            label="Dose amount"
            name="editDoseAmount"
            placeholder="1"
            type="number"
            min="0.01"
            step="0.01"
            value={editDoseAmount}
            onChange={(event) => setEditDoseAmount(event.target.value)}
            required
          />
          <div className="flex items-center justify-between rounded-2xl bg-blue-50 px-4 py-3 text-sm">
            <span className="font-medium text-slate-700">Repeats</span>
            <span className="font-semibold text-primary">Every day</span>
          </div>
          <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
            <Button variant="secondary" onClick={closeEditDialog} disabled={isUpdating}>
              Cancel
            </Button>
            <Button type="submit" disabled={isUpdating}>
              {isUpdating ? "Saving changes..." : "Save changes"}
            </Button>
          </div>
        </form>
      </Dialog>
    </UserLayout>
  );
}
