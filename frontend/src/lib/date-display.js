function coerceTimeValue(value) {
  if (!value) {
    return "";
  }

  if (/^\d{2}:\d{2}$/.test(value)) {
    return value;
  }

  if (/^\d{2}:\d{2}\s?(AM|PM)$/i.test(value)) {
    const [time, meridiem] = value.split(" ");
    const [rawHours, minutes] = time.split(":").map(Number);
    const normalizedMeridiem = meridiem.toUpperCase();
    const hours = normalizedMeridiem === "PM" && rawHours !== 12
      ? rawHours + 12
      : normalizedMeridiem === "AM" && rawHours === 12
        ? 0
        : rawHours;

    return `${String(hours).padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
  }

  return value;
}

export function formatDisplayTime(value) {
  return coerceTimeValue(value);
}

export function formatDisplayDateTime(value, locale = "en-US", timeZone = undefined) {
  if (!value) {
    return "-";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat(locale, {
    month: "short",
    day: "numeric",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
    ...(timeZone ? { timeZone } : {}),
  }).format(date);
}

export function formatTopbarDate(value = new Date(), locale = "en-US", timeZone = undefined) {
  const date = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "";
  }

  return new Intl.DateTimeFormat(locale, {
    month: "long",
    day: "numeric",
    year: "numeric",
    ...(timeZone ? { timeZone } : {}),
  }).format(date);
}
