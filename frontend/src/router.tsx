import { Navigate, createBrowserRouter } from "react-router-dom";
import type { LoginResponse } from "@/api/types";
import type { AuthSession } from "@/api/auth-session";
import { AddMedicationPage } from "@/pages/add-medication-page";
import { AdminAuditLogsPage } from "@/pages/admin-audit-logs-page";
import { AdminUserManagementPage } from "@/pages/admin-user-management-page";
import { CaregiverAccessPage } from "@/pages/caregiver-access-page";
import { CaregiverDashboardPage } from "@/pages/caregiver-dashboard-page";
import { DoseHistoryPage } from "@/pages/dose-history-page";
import { EditMedicationPage } from "@/pages/edit-medication-page";
import { LoginPage } from "@/pages/login-page";
import { MedicationListPage } from "@/pages/medication-list-page";
import { ProfilePage } from "@/pages/profile-page";
import { RefillAlertsPage } from "@/pages/refill-alerts-page";
import { RegisterPage } from "@/pages/register-page";
import { RoleManagementPage } from "@/pages/role-management-page";
import { ReportsPage } from "@/pages/reports-page";
import { ScheduleManagementPage } from "@/pages/schedule-management-page";
import { UserDashboardPage } from "@/pages/user-dashboard-page";

export function createAppRouter(
  user: LoginResponse | null,
  onLogin: (nextSession: AuthSession) => void,
) {
  return createBrowserRouter([
    {
      path: "/login",
      element: <LoginPage onLoggedIn={onLogin} />,
    },
    {
      path: "/register",
      element: <RegisterPage onRegistered={onLogin} />,
    },
    {
      path: "/",
      element: user ? (
        user.role === "USER" ? <Navigate to="/user/dashboard" replace /> :
        user.role === "CAREGIVER" ? <Navigate to="/caregiver/dashboard" replace /> :
        <Navigate to="/admin/audit-logs" replace />
      ) : <Navigate to="/login" replace />,
    },
    {
      path: "/user/dashboard",
      element: user?.role === "USER" ? <UserDashboardPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/medications",
      element: user?.role === "USER" ? <MedicationListPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/medications/new",
      element: user?.role === "USER" ? <AddMedicationPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/medications/:medicationId/edit",
      element: user?.role === "USER" ? <EditMedicationPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/schedules",
      element: user?.role === "USER" ? <ScheduleManagementPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/refills",
      element: user?.role === "USER" ? <RefillAlertsPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/history",
      element: user?.role === "USER" ? <DoseHistoryPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/caregiver-access",
      element: user?.role === "USER" ? <CaregiverAccessPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/reports",
      element: user?.role === "USER" ? <ReportsPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/user/profile",
      element: user?.role === "USER" ? <ProfilePage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/caregiver/dashboard",
      element: user?.role === "CAREGIVER" ? <CaregiverDashboardPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/admin/audit-logs",
      element: user?.role === "ADMIN" ? <AdminAuditLogsPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/admin/users",
      element: user?.role === "ADMIN" ? <AdminUserManagementPage user={user} /> : <Navigate to="/login" replace />,
    },
    {
      path: "/admin/roles",
      element: user?.role === "ADMIN" ? <RoleManagementPage user={user} /> : <Navigate to="/login" replace />,
    },
  ]);
}
