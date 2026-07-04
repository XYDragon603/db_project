import { Bell, CalendarClock, ClipboardList, HeartHandshake, Home, Pill, UserRound } from "lucide-react";
import { Link } from "react-router-dom";

const icons = {
  home: Home,
  pill: Pill,
  schedule: CalendarClock,
  alerts: Bell,
  caregiver: HeartHandshake,
  reports: ClipboardList,
  profile: UserRound,
};

export interface MobileBottomNavItem {
  label: string;
  icon: keyof typeof icons;
  active?: boolean;
  href: string;
}

export function MobileBottomNav({ items }: { items: MobileBottomNavItem[] }) {
  return (
    <div className="fixed bottom-4 left-4 right-4 z-40 rounded-[1.75rem] border border-white/80 bg-white/95 p-3 shadow-float xl:hidden">
      <div
        className="grid gap-2"
        style={{ gridTemplateColumns: `repeat(${items.length}, minmax(0, 1fr))` }}
      >
        {items.map(({ label, icon, active, href }) => {
          const Icon = icons[icon];
          return (
            <Link
            key={label}
            to={href}
            className={`flex flex-col items-center gap-1 rounded-2xl px-2 py-2 text-[11px] font-medium ${
              active ? "bg-secondary text-primary" : "text-slate-500"
            }`}
          >
            <Icon className="h-4 w-4" />
            <span>{label}</span>
            </Link>
          );
        })}
      </div>
    </div>
  );
}
