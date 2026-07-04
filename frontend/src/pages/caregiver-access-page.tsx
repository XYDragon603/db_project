import { useEffect, useState } from "react";
import {
  getCaregiverAccess,
  grantCaregiverAccess,
  revokeCaregiverAccess,
} from "@/api/client";
import type { CaregiverAccessRecord, LoginResponse } from "@/api/types";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { SectionCard } from "@/components/shared/section-card";
import { StatusBadge } from "@/components/shared/status-badge";
import { Button } from "@/components/ui/button";
import { formatDisplayDateTime } from "@/lib/date-display";
import { UserLayout } from "@/layouts/user-layout";

export function CaregiverAccessPage({ user }: { user: LoginResponse }) {
  const [records, setRecords] = useState<CaregiverAccessRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isGranting, setIsGranting] = useState(false);
  const [revokingId, setRevokingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadRecords() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getCaregiverAccess(user.userId);
        if (isMounted) {
          setRecords(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load caregiver access right now.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadRecords();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  async function handleGrant(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const caregiverEmail = String(form.get("caregiverEmail") ?? "").trim().toLowerCase();

    if (!caregiverEmail) {
      setError("Enter a caregiver email to continue.");
      setMessage(null);
      return;
    }

    setIsGranting(true);
    setError(null);
    setMessage(null);
    try {
      const saved = await grantCaregiverAccess(user.userId, caregiverEmail);
      setRecords((current) => {
        const next = current.filter((item) => item.accessId !== saved.accessId);
        return [saved, ...next];
      });
      setMessage(`Caregiver access was granted to ${saved.caregiverName}.`);
      event.currentTarget.reset();
    } catch {
      setError("Unable to grant caregiver access right now.");
    } finally {
      setIsGranting(false);
    }
  }

  async function handleRevoke(accessId: number) {
    setRevokingId(accessId);
    setError(null);
    setMessage(null);
    try {
      const updated = await revokeCaregiverAccess(user.userId, accessId);
      setRecords((current) => current.map((item) => (item.accessId === accessId ? updated : item)));
      setMessage(`Caregiver access was revoked for ${updated.caregiverName}.`);
    } catch {
      setError("Unable to revoke caregiver access right now.");
    } finally {
      setRevokingId(null);
    }
  }

  return (
    <UserLayout title="Caregiver Access">
      <PageHeader
        eyebrow="Caregiver Access"
        title="Manage read-only caregiver access"
        description="Grant a caregiver visibility into your medication routine, then revoke access any time without changing your medication records."
      />
      <div className="grid gap-5 xl:grid-cols-[minmax(0,1.2fr)_minmax(320px,0.8fr)]">
        <SectionCard
          title="Approved caregivers"
          description="These caregivers can view your dashboard summary, missed doses, and refill alerts in a read-only flow."
        >
          <div className="space-y-4">
            {error ? <FeedbackMessage message={error} variant="error" /> : null}
            {message ? <FeedbackMessage message={message} variant="success" /> : null}
            {isLoading ? (
              <LoadingState label="Loading caregiver access..." />
            ) : records.length ? (
              <div className="space-y-4">
                {records.map((record) => (
                  <div
                    key={record.accessId}
                    className="rounded-[1.5rem] border border-border bg-slate-50/80 p-4 shadow-sm"
                  >
                    <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
                      <div className="space-y-1">
                        <div className="flex flex-wrap items-center gap-3">
                          <h3 className="text-base font-semibold text-slate-900">{record.caregiverName}</h3>
                          <StatusBadge status={record.accessStatus} />
                        </div>
                        <p className="text-sm text-slate-600">{record.caregiverEmail}</p>
                        <p className="text-sm text-slate-500">
                          Granted at {formatDisplayDateTime(record.grantedAt)}
                        </p>
                      </div>
                      {record.accessStatus === "APPROVED" ? (
                        <Button
                          variant="danger"
                          onClick={() => void handleRevoke(record.accessId)}
                          disabled={revokingId === record.accessId}
                        >
                          {revokingId === record.accessId ? "Revoking..." : "Revoke access"}
                        </Button>
                      ) : (
                        <span className="text-sm font-medium text-slate-500">Access revoked</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No caregiver access yet"
                description="Add a caregiver by email to share a read-only patient overview for the caregiver dashboard."
              />
            )}
          </div>
        </SectionCard>

        <SectionCard
          title="Grant access"
          description="Use the caregiver's account email. The account must already exist with the caregiver role."
        >
          <form className="space-y-4" onSubmit={handleGrant}>
            <FormInput
              label="Caregiver email"
              name="caregiverEmail"
              placeholder="alex.caregiver@example.com"
              type="email"
              required
            />
            <Button type="submit" className="w-full" disabled={isGranting}>
              {isGranting ? "Granting..." : "Grant caregiver access"}
            </Button>
          </form>
        </SectionCard>
      </div>
    </UserLayout>
  );
}
