import { cn } from "@/lib/utils";

type FeedbackVariant = "success" | "error" | "info";

const variantStyles: Record<FeedbackVariant, string> = {
  success: "border-emerald-200 bg-emerald-50 text-emerald-700",
  error: "border-rose-200 bg-rose-50 text-rose-700",
  info: "border-sky-200 bg-sky-50 text-sky-700",
};

export function FeedbackMessage({
  message,
  variant = "info",
}: {
  message: string;
  variant?: FeedbackVariant;
}) {
  return (
    <div className={cn("rounded-2xl border px-4 py-3 text-sm font-medium", variantStyles[variant])}>
      {message}
    </div>
  );
}
