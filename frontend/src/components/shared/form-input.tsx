import { Input } from "@/components/ui/input";

export function FormInput({
  label,
  placeholder,
  type = "text",
}: {
  label: string;
  placeholder: string;
  type?: string;
}) {
  return (
    <label className="space-y-2">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <Input placeholder={placeholder} type={type} />
    </label>
  );
}
