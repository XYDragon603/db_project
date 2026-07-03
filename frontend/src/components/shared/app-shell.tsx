import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

export function AppShell({
  children,
  className,
}: {
  children: ReactNode;
  className?: string;
}) {
  return (
    <div className="min-h-screen px-4 py-4 md:px-6 md:py-6">
      <div className={cn("mx-auto w-full max-w-[1600px]", className)}>{children}</div>
    </div>
  );
}
