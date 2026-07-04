import { AlertCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";

export function RefillAlertCard({
  medication,
  currentQuantity,
  refillThreshold,
  onAddRefill,
  isSubmitting = false,
}: {
  medication: string;
  currentQuantity: number;
  refillThreshold: number;
  onAddRefill?: () => void;
  isSubmitting?: boolean;
}) {
  return (
    <Card className="space-y-4 border-danger/40 bg-gradient-to-br from-white to-rose-50">
      <div className="flex items-start gap-3">
        <div className="rounded-2xl bg-danger p-3 text-danger-foreground">
          <AlertCircle className="h-5 w-5" />
        </div>
        <div>
          <h3>{medication}</h3>
          <p>Current: {currentQuantity} tablets - Refill threshold: {refillThreshold}</p>
        </div>
      </div>
      <Button className="w-full" onClick={onAddRefill} disabled={!onAddRefill || isSubmitting}>
        {isSubmitting ? "Saving..." : "Add refill"}
      </Button>
    </Card>
  );
}
