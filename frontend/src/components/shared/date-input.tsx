import { FormInput } from "@/components/shared/form-input";
import type { InputHTMLAttributes } from "react";

export function DateInput({
  label,
  name,
  ...props
}: { label: string; name?: string } & InputHTMLAttributes<HTMLInputElement>) {
  return <FormInput label={label} name={name} placeholder="" type="date" {...props} />;
}
