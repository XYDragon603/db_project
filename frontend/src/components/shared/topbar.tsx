import { Bell, CalendarDays, Search } from "lucide-react";
import { Input } from "@/components/ui/input";

export function Topbar({
  title,
  dateLabel,
}: {
  title: string;
  dateLabel: string;
}) {
  return (
    <div className="glass-panel mb-6 flex flex-col gap-4 p-4 md:p-5 xl:flex-row xl:items-center xl:justify-between">
      <div>
        <p className="text-xs font-semibold uppercase tracking-[0.24em] text-primary">
          {title}
        </p>
        <h2 className="mt-2">Soft healthcare SaaS interface</h2>
      </div>
      <div className="flex flex-col gap-3 md:flex-row md:items-center">
        <div className="relative min-w-[240px]">
          <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
          <Input className="pl-11" placeholder="Search medications, users, or logs..." />
        </div>
        <div className="flex items-center gap-3">
          <div className="flex h-12 items-center gap-2 rounded-2xl border border-border bg-white px-4">
            <CalendarDays className="h-4 w-4 text-primary" />
            <span className="text-sm font-medium text-slate-700">{dateLabel}</span>
          </div>
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl border border-border bg-white text-slate-600">
            <Bell className="h-4 w-4" />
          </div>
        </div>
      </div>
    </div>
  );
}
