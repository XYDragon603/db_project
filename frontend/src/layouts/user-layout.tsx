import type { ReactNode } from "react";
import { useLocation } from "react-router-dom";
import { AppShell } from "@/components/shared/app-shell";
import { MobileBottomNav } from "@/components/shared/mobile-bottom-nav";
import { Sidebar } from "@/components/shared/sidebar";
import { Topbar } from "@/components/shared/topbar";
import { formatTopbarDate } from "@/lib/date-display";

export function UserLayout({
  children,
  title,
}: {
  children: ReactNode;
  title: string;
}) {
  const location = useLocation();
  const path = location.pathname;
  const dateLabel = formatTopbarDate();

  const sidebarItems = [
    { label: "Dashboard", icon: "home" as const, href: "/user/dashboard", active: path === "/user/dashboard" },
    { label: "Medications", icon: "pill" as const, href: "/user/medications", active: path.startsWith("/user/medications") },
    { label: "Schedules", icon: "schedule" as const, href: "/user/schedules", active: path.startsWith("/user/schedules") },
    { label: "Alerts", icon: "alerts" as const, href: "/user/refills", active: path.startsWith("/user/refills") },
    { label: "Reports", icon: "reports" as const, href: "/user/reports", active: path.startsWith("/user/reports") },
    { label: "Caregiver Access", icon: "users" as const, href: "/user/caregiver-access", active: path.startsWith("/user/caregiver-access") },
    { label: "Profile", icon: "profile" as const, href: "/user/profile", active: path.startsWith("/user/profile") },
  ];

  const mobileItems = [
    { label: "Home", icon: "home" as const, href: "/user/dashboard", active: path === "/user/dashboard" },
    { label: "Meds", icon: "pill" as const, href: "/user/medications", active: path.startsWith("/user/medications") },
    { label: "Schedule", icon: "schedule" as const, href: "/user/schedules", active: path.startsWith("/user/schedules") },
    { label: "Alerts", icon: "alerts" as const, href: "/user/refills", active: path.startsWith("/user/refills") },
    { label: "Reports", icon: "reports" as const, href: "/user/reports", active: path.startsWith("/user/reports") },
    { label: "Caregiver", icon: "caregiver" as const, href: "/user/caregiver-access", active: path.startsWith("/user/caregiver-access") },
    { label: "Profile", icon: "profile" as const, href: "/user/profile", active: path.startsWith("/user/profile") },
  ];

  return (
    <AppShell>
      <div className="flex gap-6">
        <Sidebar
          title="Medication Care"
          subtitle="User App"
          items={sidebarItems}
        />
        <main className="min-w-0 flex-1 pb-24 xl:pb-0">
          <Topbar title={title} dateLabel={dateLabel} />
          {children}
        </main>
      </div>
      <MobileBottomNav items={mobileItems} />
    </AppShell>
  );
}
