import type { ReactNode } from "react";
import { Card } from "@/components/ui/card";

export function StatCard({
  label,
  value,
  helper,
  icon,
}: {
  label: string;
  value: string;
  helper: string;
  icon: ReactNode;
}) {
  return (
    <Card className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="rounded-2xl bg-secondary p-3 text-primary">{icon}</div>
      </div>
      <div>
        <p className="text-sm font-medium text-slate-500">{label}</p>
        <div className="mt-2 text-3xl font-semibold text-slate-950">{value}</div>
        <p className="mt-2">{helper}</p>
      </div>
    </Card>
  );
}
