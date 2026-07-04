import test from "node:test";
import assert from "node:assert/strict";
import {
  formatDisplayDateTime,
  formatDisplayTime,
  formatTopbarDate,
} from "./date-display.js";

test("formats ISO datetime into a readable local timestamp", () => {
  assert.equal(
    formatDisplayDateTime("2026-07-03T08:02:00Z", "en-US", "UTC"),
    "Jul 3, 2026, 08:02",
  );
});

test("formats time-only strings consistently for dashboard-style screens", () => {
  assert.equal(formatDisplayTime("08:00"), "08:00");
  assert.equal(formatDisplayTime("08:00 AM"), "08:00");
});

test("formats topbar dates as a live full date label", () => {
  assert.equal(
    formatTopbarDate(new Date("2026-07-04T00:00:00Z"), "en-US", "UTC"),
    "July 4, 2026",
  );
});
