export declare function formatDoseAmountLabel(amount: string): string;
export declare function canLogDose(status: "PENDING" | "TAKEN" | "MISSED" | "SKIPPED" | "LATE"): boolean;
export declare function getDoseActionLabel(status: "PENDING" | "TAKEN" | "MISSED" | "SKIPPED" | "LATE"): string;
export declare function getScheduleMeta(frequency: string, doseAmountLabel: string): string;
