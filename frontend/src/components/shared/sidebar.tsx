import {
  Bell,
  CalendarClock,
  ClipboardList,
  Home,
  Pill,
  ShieldCheck,
  UserRound,
  Users,
} from "lucide-react";
import { Link } from "react-router-dom";
import { MedMinderLogo } from "@/components/shared/medminder-logo";
import { cn } from "@/lib/utils";

export interface SidebarItem {
  label: string;
  icon: "home" | "pill" | "schedule" | "alerts" | "reports" | "users" | "admin" | "profile";
  active?: boolean;
  href?: string;
}

const icons = {
  home: Home,
  pill: Pill,
  schedule: CalendarClock,
  alerts: Bell,
  reports: ClipboardList,
  users: Users,
  admin: ShieldCheck,
  profile: UserRound,
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
          <div className="rounded-2xl bg-white/70 p-1 shadow-sm ring-1 ring-sky-100">
            <MedMinderLogo className="h-14 w-14" />
          </div>
          <div>
            <h3 className="text-xl">MedMinder</h3>
            <p>{subtitle}</p>
          </div>
        </div>
      </div>
      <nav className="space-y-2">
        {items.map((item) => {
          const Icon = icons[item.icon];
          const className = cn(
            "flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-medium text-slate-600 transition",
            item.active && "bg-secondary text-primary shadow-sm",
          );

          const content = (
            <>
              <Icon className="h-5 w-5" />
              <span>{item.label}</span>
            </>
          );

          return item.href ? (
            <Link
              key={item.label}
              to={item.href}
              className={className}
            >
              {content}
            </Link>
          ) : (
            <div key={item.label} className={className}>
              {content}
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
