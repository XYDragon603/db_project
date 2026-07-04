import { useEffect, useState } from "react";
import { getCaregiverOverview } from "@/api/client";
import type { CaregiverPatientOverview, LoginResponse } from "@/api/types";
import { EmptyState } from "@/components/shared/empty-state";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { PatientCard } from "@/components/shared/patient-card";
import { DashboardLayout } from "@/layouts/dashboard-layout";

export function CaregiverDashboardPage({ user }: { user: LoginResponse }) {
  const [patients, setPatients] = useState<CaregiverPatientOverview[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadOverview() {
      setIsLoading(true);
      setError(null);
      try {
        const next = await getCaregiverOverview(user.userId);
        if (isMounted) {
          setPatients(next);
        }
      } catch {
        if (isMounted) {
          setError("Unable to load caregiver overview.");
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadOverview();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  return (
    <DashboardLayout
      title="Caregiver Dashboard"
      sidebarTitle="Caregiver View"
      sidebarSubtitle="Read-only Dashboard"
      items={[
        { label: "Dashboard", icon: "home", active: true },
        { label: "Patients", icon: "users" },
        { label: "Alerts", icon: "alerts" },
      ]}
    >
      <PageHeader
        eyebrow="Caregiver Dashboard"
        title="Authorized patient overview"
        description="Caregiver pages stay read-only and focus on today's risks instead of edit actions."
      />
      {error ? <FeedbackMessage message={error} variant="error" /> : null}
      {isLoading ? (
        <LoadingState label="Loading caregiver dashboard..." />
      ) : patients.length ? (
        <div className="grid gap-5 lg:grid-cols-2">
          {patients.map((patient) => (
            <PatientCard
              key={patient.patientUserId}
              name={patient.patientName}
              pending={patient.todayPending}
              missed={patient.todayMissed}
              refillAlerts={patient.refillAlerts}
            />
          ))}
        </div>
      ) : (
        <EmptyState
          title="No approved patients"
          description="Approved caregiver access records will appear here when a patient grants read-only access."
        />
      )}
    </DashboardLayout>
  );
}
