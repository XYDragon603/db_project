import { cn } from "@/lib/utils";
import medMinderLogo from "@/assets/medminder-logo.png";

export function MedMinderLogo({
  className,
  title = "MedMinder logo",
}: {
  className?: string;
  title?: string;
}) {
  return (
    <img
      src={medMinderLogo}
      alt={title}
      className={cn("h-12 w-12 shrink-0 object-contain", className)}
    />
  );
}
