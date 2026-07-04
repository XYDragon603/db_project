export function formatDoseAmountLabel(amount) {
  const numericAmount = Number(amount);
  const noun = numericAmount === 1 ? "tablet" : "tablets";
  return `${amount} ${noun}`;
}

export function canLogDose(status) {
  return status === "PENDING" || status === "LATE";
}

export function getDoseActionLabel(status) {
  if (status === "LATE") {
    return "Log as taken";
  }

  return canLogDose(status) ? "Log dose" : "Completed";
}

export function getScheduleMeta(frequency, doseAmountLabel) {
  return `${frequency} - ${doseAmountLabel}`;
}
