import { useEffect, useMemo, useState } from "react";
import { assignUserRole, getAdminRoleUsers, removeUserRole } from "@/api/client";
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

function resolveErrorMessage(error: unknown, mode: "load" | "update") {
  if (error instanceof Error && error.message.includes("403")) {
    return mode === "load"
      ? "You do not have permission to view role management."
      : "You do not have permission to change that role.";
  }
  if (error instanceof Error && error.message.includes("409")) {
    return "That user already has the CAREGIVER role.";
  }
  if (error instanceof Error && error.message.includes("400")) {
    return "That role change is not allowed in this version.";
  }

  return mode === "load"
    ? "Unable to load role management users."
    : "Unable to update that role right now.";
}

export function RoleManagementPage({ user }: { user: LoginResponse }) {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [search, setSearch] = useState("");
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
        const next = await getAdminRoleUsers();
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
    return users.filter((entry) => (
      !normalizedSearch
      || entry.fullName.toLowerCase().includes(normalizedSearch)
      || entry.email.toLowerCase().includes(normalizedSearch)
      || entry.roles.join(", ").toLowerCase().includes(normalizedSearch)
    ));
  }, [search, users]);

  async function handleRoleToggle(target: AdminUser) {
    const hasCaregiver = target.roles.includes("CAREGIVER");
    setUpdatingUserId(target.userId);
    setError(null);
    setMessage(null);
    try {
      const updated = hasCaregiver
        ? await removeUserRole(target.userId, "CAREGIVER")
        : await assignUserRole(target.userId, "CAREGIVER");

      setUsers((current) => current.map((item) => (
        item.userId === updated.userId ? updated : item
      )));
      setMessage(
        hasCaregiver
          ? `Removed CAREGIVER role from ${updated.fullName}.`
          : `Assigned CAREGIVER role to ${updated.fullName}.`,
      );
    } catch (nextError) {
      setError(resolveErrorMessage(nextError, "update"));
    } finally {
      setUpdatingUserId(null);
    }
  }

  const rows = filteredUsers.map((entry) => {
    const hasCaregiver = entry.roles.includes("CAREGIVER");
    return {
      fullName: entry.fullName,
      email: entry.email,
      roles: entry.roles.join(", "),
      status: <StatusBadge status={entry.active ? "ACTIVE" : "INACTIVE"} />,
      createdAt: formatDisplayDateTime(entry.createdAt),
      actions: entry.roles.includes("ADMIN") ? (
        <span className="text-sm font-medium text-slate-400">Protected admin role</span>
      ) : (
        <Button
          variant={hasCaregiver ? "secondary" : "primary"}
          disabled={updatingUserId === entry.userId}
          onClick={() => void handleRoleToggle(entry)}
        >
          {updatingUserId === entry.userId
            ? "Saving..."
            : hasCaregiver
              ? "Remove Caregiver Role"
              : "Add Caregiver Role"}
        </Button>
      ),
    };
  });

  return (
    <DashboardLayout
      title="Role Management"
      sidebarTitle="Admin Tools"
      sidebarSubtitle="System Dashboard"
      items={[
        { label: "Audit Logs", icon: "admin", href: "/admin/audit-logs" },
        { label: "Users", icon: "users", href: "/admin/users" },
        { label: "Role Management", icon: "reports", href: "/admin/roles", active: true },
      ]}
    >
      <PageHeader
        eyebrow="Role Management"
        title="Manage CAREGIVER access roles"
        description="This minimum version lets admins add or remove the CAREGIVER role while keeping USER as the base role and leaving ADMIN protection rules in place."
      />

      <div className="mb-5 rounded-[1.5rem] border border-blue-100 bg-blue-50/80 px-4 py-3 text-sm text-slate-600">
        Only the CAREGIVER role is editable in this version. USER remains the base role, and ADMIN role removal is protected.
      </div>

      <div className="mb-5 flex flex-col gap-3 rounded-[1.75rem] border border-white/80 bg-white/90 p-4 shadow-card md:flex-row md:items-center md:justify-between">
        <div className="w-full max-w-md">
          <Input
            placeholder="Search users by name, email, or role..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>
      </div>

      {message ? <div className="mb-5"><FeedbackMessage message={message} variant="success" /></div> : null}
      {error ? <div className="mb-5"><FeedbackMessage message={error} variant="error" /></div> : null}

      {isLoading ? (
        <LoadingState label="Loading role management users..." />
      ) : filteredUsers.length ? (
        <DataTable
          columns={[
            { key: "fullName", label: "Full name" },
            { key: "email", label: "Email" },
            { key: "roles", label: "Current roles" },
            { key: "status", label: "Status" },
            { key: "createdAt", label: "Created" },
            { key: "actions", label: "Actions", align: "right" },
          ]}
          rows={rows}
        />
      ) : (
        <EmptyState
          title="No users available for role management"
          description="Try changing the search term to bring users back into the role management table."
        />
      )}
    </DashboardLayout>
  );
}
