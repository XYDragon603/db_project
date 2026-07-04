import { useEffect, useState } from "react";
import { getDoseHistory } from "@/api/client";
import type { DoseHistoryEntry, LoginResponse } from "@/api/types";
import { DataTable } from "@/components/shared/data-table";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FilterBar } from "@/components/shared/filter-bar";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { formatDisplayDateTime } from "@/lib/date-display";
import { UserLayout } from "@/layouts/user-layout";

export function DoseHistoryPage({ user }: { user: LoginResponse }) {
  const [history, setHistory] = useState<DoseHistoryEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadHistory() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getDoseHistory(user.userId);
        if (isMounted) {
          setHistory(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load dose history.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadHistory();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  return (
    <UserLayout title="Dose History">
      <PageHeader
        eyebrow="Dose History"
        title="Medication log history"
        description="The history view stays readable on desktop and mobile while still mapping to the database reporting requirement."
      />
      <FilterBar placeholder="Search medication history..." />
      {error ? <FeedbackMessage message={error} variant="error" /> : null}
      {isLoading ? (
        <LoadingState label="Loading dose history..." />
      ) : history.length ? (
        <DataTable
          columns={[
            { key: "scheduledDatetime", label: "Scheduled" },
            { key: "medicineName", label: "Medication" },
            { key: "status", label: "Status" },
            { key: "actualTakenTime", label: "Taken at" },
          ]}
          rows={history.map((entry) => ({
            ...entry,
            scheduledDatetime: formatDisplayDateTime(entry.scheduledDatetime),
            actualTakenTime: entry.actualTakenTime ? formatDisplayDateTime(entry.actualTakenTime) : "-",
          })) as unknown as Array<Record<string, string | number>>}
        />
      ) : (
        <EmptyState
          title="No dose history available"
          description="Once you log doses from the dashboard, the completed entries will appear here."
        />
      )}
    </UserLayout>
  );
}
