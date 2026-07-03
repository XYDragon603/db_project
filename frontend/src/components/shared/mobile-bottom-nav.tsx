import { Bell, CalendarClock, Home, Pill, UserCircle2 } from "lucide-react";

const items = [
  { label: "Home", icon: Home, active: true },
  { label: "Meds", icon: Pill },
  { label: "Schedule", icon: CalendarClock },
  { label: "Alerts", icon: Bell },
  { label: "Profile", icon: UserCircle2 },
];

export function MobileBottomNav() {
  return (
    <div className="fixed bottom-4 left-4 right-4 z-40 rounded-[1.75rem] border border-white/80 bg-white/95 p-3 shadow-float xl:hidden">
      <div className="grid grid-cols-5 gap-2">
        {items.map(({ label, icon: Icon, active }) => (
          <div
            key={label}
            className={`flex flex-col items-center gap-1 rounded-2xl px-2 py-2 text-[11px] font-medium ${
              active ? "bg-secondary text-primary" : "text-slate-500"
            }`}
          >
            <Icon className="h-4 w-4" />
            <span>{label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
