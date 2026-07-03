import { Card } from "@/components/ui/card";
import { StatusBadge } from "@/components/shared/status-badge";

export function PatientCard({
  name,
  pending,
  missed,
  refillAlerts,
}: {
  name: string;
  pending: number;
  missed: number;
  refillAlerts: number;
}) {
  return (
    <Card className="space-y-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3>{name}</h3>
          <p>Authorized caregiver view</p>
        </div>
        <StatusBadge status="APPROVED" />
      </div>
      <div className="grid grid-cols-3 gap-3">
        <div className="rounded-2xl bg-muted p-3">
          <div className="text-xl font-semibold text-slate-950">{pending}</div>
          <p>Pending</p>
        </div>
        <div className="rounded-2xl bg-danger/50 p-3">
          <div className="text-xl font-semibold text-slate-950">{missed}</div>
          <p>Missed</p>
        </div>
        <div className="rounded-2xl bg-warning/60 p-3">
          <div className="text-xl font-semibold text-slate-950">{refillAlerts}</div>
          <p>Alerts</p>
        </div>
      </div>
    </Card>
  );
}
