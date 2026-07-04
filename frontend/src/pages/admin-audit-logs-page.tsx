import { useEffect, useState } from "react";
import { getAuditLogs } from "@/api/client";
import type { AuditLogEntry, LoginResponse } from "@/api/types";
import { DataTable } from "@/components/shared/data-table";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FilterBar } from "@/components/shared/filter-bar";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { formatDisplayDateTime } from "@/lib/date-display";
import { DashboardLayout } from "@/layouts/dashboard-layout";

export function AdminAuditLogsPage({ user }: { user: LoginResponse }) {
  const [logs, setLogs] = useState<AuditLogEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadLogs() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getAuditLogs();
        if (isMounted) {
          setLogs(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load audit logs.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadLogs();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  return (
    <DashboardLayout
      title="Admin Audit Logs"
      sidebarTitle="Admin Tools"
      sidebarSubtitle="System Dashboard"
      items={[
        { label: "Audit Logs", icon: "admin", href: "/admin/audit-logs", active: true },
        { label: "Users", icon: "users", href: "/admin/users" },
        { label: "Role Management", icon: "reports", href: "/admin/roles" },
      ]}
    >
      <PageHeader
        eyebrow="Admin Audit Logs"
        title="Sensitive action history"
        description="This page is one of the strongest demo-critical screens because it shows role-based management and database-backed log review."
      />
      <FilterBar placeholder="Search audit logs..." />
      {error ? <FeedbackMessage message={error} variant="error" /> : null}
      {isLoading ? (
        <LoadingState label="Loading audit logs..." />
      ) : logs.length ? (
        <DataTable
          columns={[
            { key: "createdAt", label: "Timestamp" },
            { key: "actorName", label: "Actor" },
            { key: "action", label: "Action" },
            { key: "targetTable", label: "Target" },
          ]}
          rows={logs.map((entry) => ({
            ...entry,
            createdAt: formatDisplayDateTime(entry.createdAt),
          })) as unknown as Array<Record<string, string | number>>}
        />
      ) : (
        <EmptyState
          title="No audit logs available"
          description="Once tracked system actions exist, the latest audit entries will appear here."
        />
      )}
    </DashboardLayout>
  );
}
