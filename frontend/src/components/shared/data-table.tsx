import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

type DataTableColumn<T> = {
  key: keyof T;
  label: string;
  align?: "left" | "right";
};

export function DataTable<T extends Record<string, ReactNode>>({
  columns,
  rows,
}: {
  columns: Array<DataTableColumn<T>>;
  rows: T[];
}) {
  return (
    <div className="overflow-hidden rounded-[1.75rem] border border-border bg-white">
      <div className="hidden grid-cols-[repeat(var(--cols),minmax(0,1fr))] gap-4 border-b border-border bg-slate-50 px-5 py-4 text-xs font-semibold uppercase tracking-[0.2em] text-slate-500 md:grid"
           style={{ ["--cols" as string]: columns.length }}>
        {columns.map((column) => (
          <div
            key={String(column.key)}
            className={cn(column.align === "right" && "text-right")}
          >
            {column.label}
          </div>
        ))}
      </div>
      <div className="divide-y divide-border">
        {rows.map((row, index) => (
          <div
            key={index}
            className="grid gap-3 px-5 py-4 md:grid-cols-[repeat(var(--cols),minmax(0,1fr))]"
            style={{ ["--cols" as string]: columns.length }}
          >
            {columns.map((column) => (
              <div
                key={String(column.key)}
                className={cn(
                  "text-sm text-slate-700",
                  column.align === "right" && "md:text-right",
                )}
              >
                <span className="mb-1 block text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-400 md:hidden">
                  {column.label}
                </span>
                {row[column.key]}
              </div>
            ))}
          </div>
        ))}
      </div>
    </div>
  );
}
