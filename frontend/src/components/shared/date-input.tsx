import { FormInput } from "@/components/shared/form-input";

export function DateInput({ label }: { label: string }) {
  return <FormInput label={label} placeholder="" type="date" />;
}
