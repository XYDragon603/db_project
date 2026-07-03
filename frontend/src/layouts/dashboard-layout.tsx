import type { ReactNode } from "react";
import { AppShell } from "@/components/shared/app-shell";
import { Sidebar, type SidebarItem } from "@/components/shared/sidebar";
import { Topbar } from "@/components/shared/topbar";

export function DashboardLayout({
  children,
  title,
  sidebarTitle,
  sidebarSubtitle,
  items,
}: {
  children: ReactNode;
  title: string;
  sidebarTitle: string;
  sidebarSubtitle: string;
  items: SidebarItem[];
}) {
  return (
    <AppShell>
      <div className="flex gap-6">
        <Sidebar title={sidebarTitle} subtitle={sidebarSubtitle} items={items} />
        <main className="min-w-0 flex-1">
          <Topbar title={title} dateLabel="July 2, 2026" />
          {children}
        </main>
      </div>
    </AppShell>
  );
}
