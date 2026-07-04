import { useEffect, useMemo, useState } from "react";
import { deactivateUser, getAdminUsers, reactivateUser } from "@/api/client";
import type { AdminUser, LoginResponse } from "@/api/types";
import { DataTable } from "@/components/shared/data-table";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { StatusBadge } from "@/components/shared/status-badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { formatDisplayDateTime } from "@/lib/date-display";
import { DashboardLayout } from "@/layouts/dashboard-layout";

type StatusFilter = "ALL" | "ACTIVE" | "INACTIVE";

function resolveErrorMessage(error: unknown, mode: "load" | "update") {
  if (error instanceof Error && error.message.includes("403")) {
    return mode === "load"
      ? "You do not have permission to view user management."
      : "You do not have permission to change that user account.";
  }

  return mode === "load"
    ? "Unable to load user accounts."
    : "Unable to update that user account right now.";
}

export function AdminUserManagementPage({ user }: { user: LoginResponse }) {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");
  const [isLoading, setIsLoading] = useState(true);
  const [updatingUserId, setUpdatingUserId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadUsers() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getAdminUsers();
        if (isMounted) {
          setUsers(next);
        }
      } catch (nextError) {
        if (isMounted) {
          setError(resolveErrorMessage(nextError, "load"));
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadUsers();

    return () => {
      isMounted = false;
    };
  }, []);

  const filteredUsers = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return users.filter((entry) => {
      const matchesSearch = !normalizedSearch
        || entry.fullName.toLowerCase().includes(normalizedSearch)
        || entry.email.toLowerCase().includes(normalizedSearch)
        || (entry.phone ?? "").toLowerCase().includes(normalizedSearch);

      const matchesStatus = statusFilter === "ALL"
        || (statusFilter === "ACTIVE" && entry.active)
        || (statusFilter === "INACTIVE" && !entry.active);

      return matchesSearch && matchesStatus;
    });
  }, [search, statusFilter, users]);

  async function handleStatusChange(target: AdminUser) {
    setUpdatingUserId(target.userId);
    setError(null);
    setMessage(null);
    try {
      const updated = target.active
        ? await deactivateUser(target.userId)
        : await reactivateUser(target.userId);

      setUsers((current) => current.map((item) => (
        item.userId === updated.userId ? updated : item
      )));
      setMessage(
        updated.active
          ? `${updated.fullName} was reactivated successfully.`
          : `${updated.fullName} was deactivated successfully.`,
      );
    } catch (nextError) {
      setError(resolveErrorMessage(nextError, "update"));
    } finally {
      setUpdatingUserId(null);
    }
  }

  const rows = filteredUsers.map((entry) => ({
    fullName: entry.fullName,
    email: entry.email,
    phone: entry.phone ?? "-",
    status: <StatusBadge status={entry.active ? "ACTIVE" : "INACTIVE"} />,
    roles: entry.roles.join(", "),
    createdAt: formatDisplayDateTime(entry.createdAt),
    actions: entry.userId === user.userId ? (
      <span className="text-sm font-medium text-slate-400">Current admin account</span>
    ) : (
      <Button
        variant={entry.active ? "danger" : "secondary"}
        disabled={updatingUserId === entry.userId}
        onClick={() => void handleStatusChange(entry)}
      >
        {updatingUserId === entry.userId
          ? "Saving..."
          : entry.active
            ? "Deactivate"
            : "Reactivate"}
      </Button>
    ),
  }));

  return (
    <DashboardLayout
      title="Admin User Management"
      sidebarTitle="Admin Tools"
      sidebarSubtitle="System Dashboard"
      items={[
        { label: "Audit Logs", icon: "admin", href: "/admin/audit-logs" },
        { label: "Users", icon: "users", href: "/admin/users", active: true },
        { label: "Role Management", icon: "reports", href: "/admin/roles" },
      ]}
    >
      <PageHeader
        eyebrow="Admin User Management"
        title="Basic account status management"
        description="Review user accounts, confirm roles, and activate or deactivate access without editing private medication records."
      />

      <div className="mb-5 flex flex-col gap-3 rounded-[1.75rem] border border-white/80 bg-white/90 p-4 shadow-card md:flex-row md:items-center md:justify-between">
        <div className="w-full max-w-md">
          <Input
            placeholder="Search users by name, email, or phone..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>
        <div className="flex items-center gap-3">
          <select
            className="h-12 rounded-2xl border border-border bg-white px-4 text-sm text-slate-700 shadow-sm outline-none"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value as StatusFilter)}
          >
            <option value="ALL">All statuses</option>
            <option value="ACTIVE">Active only</option>
            <option value="INACTIVE">Inactive only</option>
          </select>
        </div>
      </div>

      {message ? <div className="mb-5"><FeedbackMessage message={message} variant="success" /></div> : null}
      {error ? <div className="mb-5"><FeedbackMessage message={error} variant="error" /></div> : null}

      {isLoading ? (
        <LoadingState label="Loading user accounts..." />
      ) : filteredUsers.length ? (
        <DataTable
          columns={[
            { key: "fullName", label: "Full name" },
            { key: "email", label: "Email" },
            { key: "phone", label: "Phone" },
            { key: "status", label: "Status" },
            { key: "roles", label: "Roles" },
            { key: "createdAt", label: "Created" },
            { key: "actions", label: "Actions", align: "right" },
          ]}
          rows={rows}
        />
      ) : (
        <EmptyState
          title="No user accounts match this view"
          description="Adjust the search or status filter to bring user accounts back into the table."
        />
      )}
    </DashboardLayout>
  );
}
