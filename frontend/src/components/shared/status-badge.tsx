import { cn } from "@/lib/utils";

type Status =
  | "PENDING"
  | "TAKEN"
  | "MISSED"
  | "SKIPPED"
  | "LATE"
  | "APPROVED"
  | "REVOKED";

const badgeStyles: Record<Status, string> = {
  PENDING: "bg-warning text-warning-foreground",
  TAKEN: "bg-success text-success-foreground",
  MISSED: "bg-danger text-danger-foreground",
  SKIPPED: "bg-slate-100 text-slate-600",
  LATE: "bg-amber-100 text-amber-700",
  APPROVED: "bg-success text-success-foreground",
  REVOKED: "bg-danger text-danger-foreground",
};

export function StatusBadge({ status }: { status: Status }) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold uppercase tracking-wide",
        badgeStyles[status],
      )}
    >
      {status}
    </span>
  );
}
