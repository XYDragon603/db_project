import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { AppShell } from "@/components/shared/app-shell";
import { Sidebar, type SidebarItem } from "@/components/shared/sidebar";
import { Topbar } from "@/components/shared/topbar";
import { useDemoAuth } from "@/hooks/use-demo-auth";
import { formatTopbarDate } from "@/lib/date-display";

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
  const navigate = useNavigate();
  const auth = useDemoAuth();
  const dateLabel = formatTopbarDate();

  function handleLogout() {
    auth.logout();
    navigate("/login", { replace: true });
  }

  return (
    <AppShell>
      <div className="flex gap-6">
        <Sidebar title={sidebarTitle} subtitle={sidebarSubtitle} items={items} />
        <main className="min-w-0 flex-1">
          <Topbar title={title} dateLabel={dateLabel} onLogout={handleLogout} />
          {children}
        </main>
      </div>
    </AppShell>
  );
}
