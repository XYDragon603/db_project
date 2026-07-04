import type { SelectHTMLAttributes } from "react";
import { Select } from "@/components/ui/select";

export function FormSelect({
  label,
  options,
  name,
  ...props
}: {
  label: string;
  options: string[];
  name?: string;
} & SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <label className="space-y-2">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <Select defaultValue={options[0]} name={name} {...props}>
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </Select>
    </label>
  );
}
