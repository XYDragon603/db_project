import { Input } from "@/components/ui/input";
import type { InputHTMLAttributes } from "react";

export function FormInput({
  label,
  placeholder,
  type = "text",
  name,
  ...props
}: {
  label: string;
  placeholder: string;
  type?: string;
  name?: string;
} & InputHTMLAttributes<HTMLInputElement>) {
  return (
    <label className="space-y-2">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <Input name={name} placeholder={placeholder} type={type} {...props} />
    </label>
  );
}
