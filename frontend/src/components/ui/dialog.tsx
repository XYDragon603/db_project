import { X } from "lucide-react";
import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

interface DialogProps {
  open?: boolean;
  title: string;
  description?: string;
  children: ReactNode;
  className?: string;
  onClose?: () => void;
}

export function Dialog({
  open = true,
  title,
  description,
  children,
  className,
  onClose,
}: DialogProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/30 p-4" role="presentation" onMouseDown={onClose}>
      <div
        className={cn(
          "max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-[2rem] border border-white/80 bg-white p-6 shadow-float",
          className,
        )}
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mb-4 flex items-start justify-between gap-4">
          <div>
            <h3 id="dialog-title">{title}</h3>
            {description ? <p className="mt-1">{description}</p> : null}
          </div>
          <button aria-label="Close dialog" className="rounded-full bg-slate-100 p-2 text-slate-500 hover:bg-slate-200" onClick={onClose} type="button">
            <X className="h-4 w-4" />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}
