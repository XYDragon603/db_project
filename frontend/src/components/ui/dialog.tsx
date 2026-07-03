import { X } from "lucide-react";
import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

interface DialogProps {
  open?: boolean;
  title: string;
  description?: string;
  children: ReactNode;
  className?: string;
}

export function Dialog({
  open = true,
  title,
  description,
  children,
  className,
}: DialogProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 hidden items-center justify-center bg-slate-950/20 p-4 xl:flex">
      <div
        className={cn(
          "w-full max-w-lg rounded-[2rem] border border-white/80 bg-white p-6 shadow-float",
          className,
        )}
      >
        <div className="mb-4 flex items-start justify-between gap-4">
          <div>
            <h3>{title}</h3>
            {description ? <p className="mt-1">{description}</p> : null}
          </div>
          <span className="rounded-full bg-slate-100 p-2 text-slate-500">
            <X className="h-4 w-4" />
          </span>
        </div>
        {children}
      </div>
    </div>
  );
}
