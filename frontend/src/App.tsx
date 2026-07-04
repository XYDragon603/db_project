import { RouterProvider } from "react-router-dom";
import { useMemo } from "react";
import { useDemoAuth } from "@/hooks/use-demo-auth";
import { createAppRouter } from "@/router";

export default function App() {
  const auth = useDemoAuth();
  const router = useMemo(() => createAppRouter(auth.user, auth.login), [auth]);

  return <RouterProvider router={router} />;
}
