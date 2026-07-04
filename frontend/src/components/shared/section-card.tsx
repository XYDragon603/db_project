import type { ReactNode } from "react";
import { Card } from "@/components/ui/card";

export function SectionCard({
  title,
  description,
  children,
}: {
  title: string;
  description?: string;
  children: ReactNode;
}) {
  return (
    <Card className="space-y-4">
      <div>
        <h3>{title}</h3>
        {description ? <p className="mt-1">{description}</p> : null}
      </div>
      {children}
    </Card>
  );
}
