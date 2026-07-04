import test from "node:test";
import assert from "node:assert/strict";
import { resolveMockFallbackEnabled } from "./runtime-config.js";

test("disables mock fallback by default for real API first behavior", () => {
  assert.equal(resolveMockFallbackEnabled(undefined), false);
  assert.equal(resolveMockFallbackEnabled("false"), false);
});

test("enables mock fallback only when explicitly set to true", () => {
  assert.equal(resolveMockFallbackEnabled("true"), true);
  assert.equal(resolveMockFallbackEnabled("TRUE"), true);
});
