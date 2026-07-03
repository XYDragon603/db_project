import { Select } from "@/components/ui/select";

export function FormSelect({
  label,
  options,
}: {
  label: string;
  options: string[];
}) {
  return (
    <label className="space-y-2">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <Select defaultValue={options[0]}>
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </Select>
    </label>
  );
}
