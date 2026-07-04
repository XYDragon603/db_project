import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { StatusBadge } from "@/components/shared/status-badge";

export function ScheduleCard({
  time,
  meta,
  active,
  action,
}: {
  time: string;
  meta: string;
  active: boolean;
  action?: {
    label: string;
    onClick: () => void;
    disabled?: boolean;
  };
}) {
  return (
    <Card className="flex items-center justify-between gap-4">
      <div>
        <h3>{time}</h3>
        <p>{meta}</p>
      </div>
      <div className="flex items-center gap-3">
        <StatusBadge status={active ? "ACTIVE" : "INACTIVE"} />
        {action ? (
          <Button variant="danger" disabled={action.disabled} onClick={action.onClick}>
            {action.label}
          </Button>
        ) : null}
      </div>
    </Card>
  );
}
