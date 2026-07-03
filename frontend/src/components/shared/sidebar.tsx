import {
  Bell,
  CalendarClock,
  ClipboardList,
  Home,
  Pill,
  ShieldCheck,
  Users,
} from "lucide-react";
import { cn } from "@/lib/utils";

export interface SidebarItem {
  label: string;
  icon: "home" | "pill" | "schedule" | "alerts" | "reports" | "users" | "admin";
  active?: boolean;
}

const icons = {
  home: Home,
  pill: Pill,
  schedule: CalendarClock,
  alerts: Bell,
  reports: ClipboardList,
  users: Users,
  admin: ShieldCheck,
};

export function Sidebar({
  title,
  subtitle,
  items,
}: {
  title: string;
  subtitle: string;
  items: SidebarItem[];
}) {
  return (
    <aside className="hidden w-72 shrink-0 rounded-[2rem] border border-white/80 bg-white/90 p-6 shadow-card xl:flex xl:flex-col">
      <div className="mb-8">
        <div className="flex items-center gap-3">
          <div className="h-12 w-12 rounded-2xl bg-gradient-to-br from-sky-300 via-blue-400 to-blue-600" />
          <div>
            <h3 className="text-xl">MedMinder</h3>
            <p>{subtitle}</p>
          </div>
        </div>
      </div>
      <nav className="space-y-2">
        {items.map((item) => {
          const Icon = icons[item.icon];
          return (
            <div
              key={item.label}
              className={cn(
                "flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-medium text-slate-600 transition",
                item.active && "bg-secondary text-primary shadow-sm",
              )}
            >
              <Icon className="h-5 w-5" />
              <span>{item.label}</span>
            </div>
          );
        })}
      </nav>
      <div className="mt-auto rounded-[1.75rem] bg-gradient-to-br from-blue-50 to-white p-4">
        <p className="text-sm font-semibold text-slate-900">{title}</p>
        <p className="mt-1">Stay on track with a calm and consistent care routine.</p>
      </div>
    </aside>
  );
}
