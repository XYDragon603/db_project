import { Activity, AlertTriangle, CheckCircle2, Clock3, ClipboardList, SkipForward } from "lucide-react";
import { useEffect, useState } from "react";
import { getAdherenceReport } from "@/api/client";
import type { AdherenceReport, LoginResponse } from "@/api/types";
import { DataTable } from "@/components/shared/data-table";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { StatCard } from "@/components/shared/stat-card";
import { UserLayout } from "@/layouts/user-layout";

function getCurrentMonthValue() {
  return new Date().toISOString().slice(0, 7);
}

export function ReportsPage({ user }: { user: LoginResponse }) {
  const [month, setMonth] = useState(getCurrentMonthValue);
  const [report, setReport] = useState<AdherenceReport | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadReport() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getAdherenceReport(user.userId, month);
        if (isMounted) {
          setReport(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load the adherence report for this month.");
          setReport(null);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadReport();

    return () => {
      isMounted = false;
    };
  }, [month, user.userId]);

  const summary = report?.summary;

  return (
    <UserLayout title="Reports">
      <PageHeader
        eyebrow="Reports"
        title="Monthly adherence report"
        description="Review the selected month using real schedule and dose log data, then track how the counts change as new doses are logged."
      />
      <div className="mb-5 flex justify-end">
        <div className="w-full max-w-xs">
          <FormInput
            label="Report month"
            name="month"
            type="month"
            placeholder="Select month"
            value={month}
            onChange={(event) => setMonth(event.target.value)}
          />
        </div>
      </div>

      {error ? <FeedbackMessage message={error} variant="error" /> : null}

      {isLoading ? (
        <LoadingState label="Loading monthly report..." />
      ) : report && summary ? (
        <div className="space-y-5">
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            <StatCard
              label="Adherence rate"
              value={`${summary.adherenceRate.toFixed(2)}%`}
              helper={`Month: ${report.month}`}
              icon={<Activity className="h-5 w-5" />}
            />
            <StatCard
              label="Scheduled doses"
              value={String(summary.totalScheduledDoses)}
              helper="Derived from active daily schedules in the selected month."
              icon={<ClipboardList className="h-5 w-5" />}
            />
            <StatCard
              label="Taken doses"
              value={String(summary.totalTakenDoses)}
              helper="Recorded as TAKEN in dose logs."
              icon={<CheckCircle2 className="h-5 w-5" />}
            />
            <StatCard
              label="Missed doses"
              value={String(summary.totalMissedDoses)}
              helper="Recorded as MISSED in dose logs."
              icon={<AlertTriangle className="h-5 w-5" />}
            />
            <StatCard
              label="Skipped doses"
              value={String(summary.totalSkippedDoses)}
              helper="Recorded as SKIPPED in dose logs."
              icon={<SkipForward className="h-5 w-5" />}
            />
            <StatCard
              label="Late doses"
              value={String(summary.totalLateDoses)}
              helper="Recorded as LATE in dose logs."
              icon={<Clock3 className="h-5 w-5" />}
            />
          </div>

          {report.medications.length ? (
            <DataTable
              columns={[
                { key: "medicineName", label: "Medication" },
                { key: "scheduledDoses", label: "Scheduled", align: "right" },
                { key: "takenCount", label: "Taken", align: "right" },
                { key: "missedCount", label: "Missed", align: "right" },
                { key: "skippedCount", label: "Skipped", align: "right" },
                { key: "lateCount", label: "Late", align: "right" },
                { key: "adherenceRate", label: "Adherence", align: "right" },
              ]}
              rows={report.medications.map((item) => ({
                medicineName: `${item.medicineName} ${item.dosage}`,
                scheduledDoses: item.scheduledDoses,
                takenCount: item.takenCount,
                missedCount: item.missedCount,
                skippedCount: item.skippedCount,
                lateCount: item.lateCount,
                adherenceRate: `${item.adherenceRate.toFixed(2)}%`,
              }))}
            />
          ) : (
            <EmptyState
              title="No medication summary for this month"
              description="Add active medication schedules and dose logs to populate the medication-level report table."
            />
          )}
        </div>
      ) : (
        <EmptyState
          title="No report data available"
          description="This month does not have any active schedules or dose logs for the selected user."
        />
      )}
    </UserLayout>
  );
}
