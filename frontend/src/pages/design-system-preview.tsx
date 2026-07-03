import {
  Activity,
  ClipboardList,
  ShieldCheck,
  Users,
} from "lucide-react";
import { DateInput } from "@/components/shared/date-input";
import { EmptyState } from "@/components/shared/empty-state";
import { FilterBar } from "@/components/shared/filter-bar";
import { FormInput } from "@/components/shared/form-input";
import { FormSelect } from "@/components/shared/form-select";
import { MedicationCard } from "@/components/shared/medication-card";
import { PageHeader } from "@/components/shared/page-header";
import { PatientCard } from "@/components/shared/patient-card";
import { RefillAlertCard } from "@/components/shared/refill-alert-card";
import { ScheduleCard } from "@/components/shared/schedule-card";
import { StatCard } from "@/components/shared/stat-card";
import { DataTable } from "@/components/shared/data-table";
import { DashboardLayout } from "@/layouts/dashboard-layout";
import { UserLayout } from "@/layouts/user-layout";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Dialog } from "@/components/ui/dialog";
import { auditRows, medications, patients, refillAlerts, schedules } from "@/data/demo";

function UserPreview() {
  return (
    <UserLayout title="User Experience">
      <PageHeader
        eyebrow="User App"
        title="Today's Medications"
        description="A mobile-first medication reminder layout with large cards, clear actions, and a calm healthcare tone."
        actions={<Button>View full schedule</Button>}
      />
      <div className="grid gap-5 xl:grid-cols-[1.6fr_1fr]">
        <div className="space-y-5">
          <div className="grid gap-4">
            {medications.map((item) => (
              <MedicationCard key={`${item.name}-${item.time}`} {...item} />
            ))}
          </div>
          <Card className="space-y-4">
            <h3>Schedule Management Preview</h3>
            <div className="grid gap-3">
              {schedules.map((schedule) => (
                <ScheduleCard key={schedule.time} {...schedule} />
              ))}
            </div>
          </Card>
        </div>
        <div className="space-y-5">
          <StatCard
            label="Adherence"
            value="87%"
            helper="47 taken doses out of 54 scheduled this month."
            icon={<Activity className="h-5 w-5" />}
          />
          {refillAlerts.map((item) => (
            <RefillAlertCard key={item.medication} {...item} />
          ))}
        </div>
      </div>
    </UserLayout>
  );
}

function CaregiverPreview() {
  return (
    <DashboardLayout
      title="Caregiver Experience"
      sidebarTitle="Caregiver View"
      sidebarSubtitle="Read-only Dashboard"
      items={[
        { label: "Dashboard", icon: "home", active: true },
        { label: "Patients", icon: "users" },
        { label: "Alerts", icon: "alerts" },
      ]}
    >
      <PageHeader
        eyebrow="Caregiver App"
        title="Authorized Patient Overview"
        description="Patient cards emphasize pending doses, missed doses, and refill risk without exposing edit actions."
      />
      <div className="grid gap-5 lg:grid-cols-2">
        {patients.map((patient) => (
          <PatientCard key={patient.name} {...patient} />
        ))}
      </div>
    </DashboardLayout>
  );
}

function AdminPreview() {
  return (
    <DashboardLayout
      title="Admin Experience"
      sidebarTitle="Admin Tools"
      sidebarSubtitle="System Dashboard"
      items={[
        { label: "Dashboard", icon: "admin", active: true },
        { label: "Users", icon: "users" },
        { label: "Reports", icon: "reports" },
      ]}
    >
      <PageHeader
        eyebrow="Admin App"
        title="Audit Review and Styled Tables"
        description="The admin surface stays clean and focused on system summaries, filters, and readable operational tables."
      />
      <div className="grid gap-5 xl:grid-cols-3">
        <StatCard label="Active Users" value="124" helper="Across user, caregiver, and admin roles." icon={<Users className="h-5 w-5" />} />
        <StatCard label="Audit Events" value="382" helper="Recent activity across medication and access actions." icon={<ShieldCheck className="h-5 w-5" />} />
        <StatCard label="Reports Ready" value="12" helper="Monthly summaries prepared for review." icon={<ClipboardList className="h-5 w-5" />} />
      </div>
      <FilterBar>
        <div className="w-full md:w-44">
          <FormSelect label="Action" options={["All actions", "LOG_DOSE", "ADD_REFILL"]} />
        </div>
      </FilterBar>
      <DataTable
        columns={[
          { key: "timestamp", label: "Timestamp" },
          { key: "actor", label: "Actor" },
          { key: "action", label: "Action" },
          { key: "target", label: "Target" },
        ]}
        rows={auditRows}
      />
    </DashboardLayout>
  );
}

export function DesignSystemPreview() {
  return (
    <div className="space-y-8">
      <UserPreview />
      <CaregiverPreview />
      <AdminPreview />
      <div className="mx-auto max-w-[1600px] px-4 pb-8 md:px-6">
        <div className="grid gap-6 xl:grid-cols-[1.3fr_1fr]">
          <Card className="space-y-5">
            <PageHeader
              eyebrow="Shared Forms"
              title="Form and empty states"
              description="Sprint 1 includes reusable form controls so later pages stay visually consistent."
            />
            <div className="grid gap-4 md:grid-cols-2">
              <FormInput label="Medication name" placeholder="Enter medication name" />
              <FormInput label="Dosage" placeholder="500mg" />
              <FormSelect label="Form" options={["Tablet", "Capsule", "Liquid"]} />
              <DateInput label="Start date" />
            </div>
            <div className="flex gap-3">
              <Button>Primary action</Button>
              <Button variant="secondary">Secondary action</Button>
              <Button variant="danger">Danger action</Button>
            </div>
          </Card>
          <EmptyState
            title="No medications added yet"
            description="The empty state language stays helpful and polished instead of showing a blank or generic screen."
          />
        </div>
      </div>
      <Dialog
        title="Dose logging modal preview"
        description="A lightweight modal pattern is available for quick actions on larger screens."
      >
        <div className="space-y-3">
          <p>Use this for dose logging, refill confirmation, or caregiver access review later.</p>
          <div className="flex gap-3">
            <Button>Confirm</Button>
            <Button variant="secondary">Cancel</Button>
          </div>
        </div>
      </Dialog>
    </div>
  );
}
