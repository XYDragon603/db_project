export function LoadingState({ label = "Loading..." }: { label?: string }) {
  return (
    <div className="rounded-[1.75rem] border border-dashed border-border bg-white/70 p-8 text-center text-sm font-medium text-slate-500">
      {label}
    </div>
  );
}
