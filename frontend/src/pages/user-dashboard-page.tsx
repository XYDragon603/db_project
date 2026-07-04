import { useEffect, useState } from "react";
import { Activity } from "lucide-react";
import { Link } from "react-router-dom";
import { buildDoseLogPayload, getDashboard, logDose } from "@/api/client";
import type { DashboardData, DoseLogStatus, LoginResponse } from "@/api/types";
import { Button } from "@/components/ui/button";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { LoadingState } from "@/components/shared/loading-state";
import { MedicationCard } from "@/components/shared/medication-card";
import { PageHeader } from "@/components/shared/page-header";
import { RefillAlertCard } from "@/components/shared/refill-alert-card";
import { SectionCard } from "@/components/shared/section-card";
import { StatCard } from "@/components/shared/stat-card";
import { formatDoseAmountLabel } from "@/features/medications/display-utils";
import { UserLayout } from "@/layouts/user-layout";

const SECONDARY_DOSE_ACTIONS: DoseLogStatus[] = ["MISSED", "SKIPPED", "LATE"];

export function UserDashboardPage({ user }: { user: LoginResponse }) {
  const [data, setData] = useState<DashboardData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [submittingKey, setSubmittingKey] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadDashboard() {
      setIsLoading(true);
      setError(null);
      setMessage(null);
      try {
        const next = await getDashboard(user.userId);
        if (isMounted) {
          setData(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load today's medication overview.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadDashboard();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  async function handleDoseAction(scheduleId: number, status: DoseLogStatus) {
    setSubmittingKey(`${scheduleId}-${status}`);
    setError(null);
    setMessage(null);
    try {
      await logDose(user.userId, buildDoseLogPayload(scheduleId, status));
      const next = await getDashboard(user.userId);
      setData(next);
      setMessage(`Dose recorded as ${status.toLowerCase()}.`);
    } catch {
      setError("Unable to save that dose right now.");
    } finally {
      setSubmittingKey(null);
    }
  }

  return (
    <UserLayout title="User Dashboard">
      <PageHeader
        eyebrow="Today's Medications"
        title={`Good morning, ${user.fullName.split(" ")[0]}`}
        description="Your demo-critical dashboard keeps today's medication tasks front and center."
      />
      {error ? <div className="mb-5"><FeedbackMessage message={error} variant="error" /></div> : null}
      {message ? <div className="mb-5"><FeedbackMessage message={message} variant="success" /></div> : null}
      <div className="grid gap-5 xl:grid-cols-[1.6fr_1fr]">
        <SectionCard title="Today's Medications">
          {isLoading ? (
            <LoadingState label="Loading today's medication cards..." />
          ) : data?.todaysMedications.length ? (
            <div className="grid gap-4">
              {data.todaysMedications.map((item) => (
                <MedicationCard
                  key={item.scheduleId}
                  name={item.medicineName}
                  dosage={item.dosage}
                  time={item.scheduledTime}
                  amount={formatDoseAmountLabel(item.doseAmount)}
                  status={item.status}
                  onLogDose={() => handleDoseAction(item.scheduleId, "TAKEN")}
                  isSubmitting={submittingKey === `${item.scheduleId}-TAKEN`}
                  supportingActions={
                    item.status === "PENDING" || item.status === "LATE" ? (
                      <>
                        {SECONDARY_DOSE_ACTIONS.map((status) => (
                          <Button
                            key={status}
                            variant="ghost"
                            disabled={submittingKey !== null}
                            onClick={() => handleDoseAction(item.scheduleId, status)}
                          >
                            Mark {status.toLowerCase()}
                          </Button>
                        ))}
                      </>
                    ) : undefined
                  }
                />
              ))}
            </div>
          ) : (
            <EmptyState
              title="No medications scheduled today"
              description="When a daily schedule is active, today's medication cards will appear here."
              action={(
                <Link to="/user/schedules">
                  <Button>Open schedules</Button>
                </Link>
              )}
            />
          )}
        </SectionCard>
        <div className="space-y-5">
          <StatCard
            label="Adherence"
            value={`${Math.round(data?.adherenceSummary.adherenceRate ?? 0)}%`}
            helper={`${data?.adherenceSummary.totalTakenDoses ?? 0} taken doses this month`}
            icon={<Activity className="h-5 w-5" />}
          />
          <SectionCard title="Refill Alerts">
            {isLoading ? (
              <LoadingState label="Checking refill thresholds..." />
            ) : data?.refillAlerts.length ? (
              <div className="grid gap-4">
                {data.refillAlerts.map((alert) => (
                  <RefillAlertCard
                    key={alert.medicationId}
                    medication={alert.medicineName}
                    currentQuantity={alert.currentQuantity}
                    refillThreshold={alert.refillThreshold}
                  />
                ))}
              </div>
            ) : (
              <EmptyState
                title="No refill alerts"
                description="Your active medications are above their refill thresholds right now."
              />
            )}
          </SectionCard>
        </div>
      </div>
    </UserLayout>
  );
}
