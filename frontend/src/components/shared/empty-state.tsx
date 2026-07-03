import { Inbox } from "lucide-react";
import { Card } from "@/components/ui/card";

export function EmptyState({
  title,
  description,
}: {
  title: string;
  description: string;
}) {
  return (
    <Card className="flex flex-col items-center justify-center gap-3 py-10 text-center">
      <div className="rounded-full bg-secondary p-4 text-primary">
        <Inbox className="h-6 w-6" />
      </div>
      <h3>{title}</h3>
      <p className="max-w-md">{description}</p>
    </Card>
  );
}
