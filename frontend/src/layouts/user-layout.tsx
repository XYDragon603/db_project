import type { ReactNode } from "react";
import { AppShell } from "@/components/shared/app-shell";
import { MobileBottomNav } from "@/components/shared/mobile-bottom-nav";
import { Sidebar } from "@/components/shared/sidebar";
import { Topbar } from "@/components/shared/topbar";

export function UserLayout({
  children,
  title,
}: {
  children: ReactNode;
  title: string;
}) {
  return (
    <AppShell>
      <div className="flex gap-6">
        <Sidebar
          title="Medication Care"
          subtitle="User App"
          items={[
            { label: "Dashboard", icon: "home", active: true },
            { label: "Medications", icon: "pill" },
            { label: "Schedules", icon: "schedule" },
            { label: "Alerts", icon: "alerts" },
            { label: "Reports", icon: "reports" },
          ]}
        />
        <main className="min-w-0 flex-1 pb-24 xl:pb-0">
          <Topbar title={title} dateLabel="July 2, 2026" />
          {children}
        </main>
      </div>
      <MobileBottomNav />
    </AppShell>
  );
}
