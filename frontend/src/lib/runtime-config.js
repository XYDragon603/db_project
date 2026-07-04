export function resolveMockFallbackEnabled(rawValue) {
  return typeof rawValue === "string" && rawValue.trim().toLowerCase() === "true";
}
