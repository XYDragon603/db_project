import { Clock3, Pill } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { StatusBadge } from "@/components/shared/status-badge";

export interface MedicationCardProps {
  name: string;
  dosage: string;
  time: string;
  amount: string;
  status: "PENDING" | "TAKEN" | "MISSED" | "SKIPPED" | "LATE";
  readOnly?: boolean;
}

export function MedicationCard({
  name,
  dosage,
  time,
  amount,
  status,
  readOnly = false,
}: MedicationCardProps) {
  return (
    <Card className="space-y-4">
      <div className="flex items-start justify-between gap-4">
        <div className="flex gap-4">
          <div className="rounded-2xl bg-accent p-3 text-primary">
            <Pill className="h-5 w-5" />
          </div>
          <div>
            <div className="text-lg font-semibold text-slate-950">{name}</div>
            <p>{dosage} · {amount}</p>
          </div>
        </div>
        <StatusBadge status={status} />
      </div>
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-2 text-sm font-medium text-slate-600">
          <Clock3 className="h-4 w-4 text-primary" />
          {time}
        </div>
        {readOnly ? (
          <span className="text-sm font-medium text-slate-500">Read only</span>
        ) : (
          <Button variant="secondary">Log dose</Button>
        )}
      </div>
    </Card>
  );
}
