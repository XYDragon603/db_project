import { Card } from "@/components/ui/card";
import { StatusBadge } from "@/components/shared/status-badge";

export function ScheduleCard({
  time,
  frequency,
  doseAmount,
  active,
}: {
  time: string;
  frequency: string;
  doseAmount: string;
  active: boolean;
}) {
  return (
    <Card className="flex items-center justify-between gap-4">
      <div>
        <h3>{time}</h3>
        <p>{frequency} · {doseAmount}</p>
      </div>
      <StatusBadge status={active ? "APPROVED" : "REVOKED"} />
    </Card>
  );
}
