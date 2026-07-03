import type { ReactNode } from "react";
import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";

export function FilterBar({
  children,
  placeholder = "Search records...",
}: {
  children?: ReactNode;
  placeholder?: string;
}) {
  return (
    <div className="mb-5 flex flex-col gap-3 rounded-[1.75rem] border border-white/80 bg-white/90 p-4 shadow-card md:flex-row md:items-center md:justify-between">
      <div className="relative w-full max-w-md">
        <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
        <Input className="pl-11" placeholder={placeholder} />
      </div>
      <div className="flex flex-wrap items-center gap-3">{children}</div>
    </div>
  );
}
