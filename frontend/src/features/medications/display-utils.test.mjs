import test from "node:test";
import assert from "node:assert/strict";
import {
  canLogDose,
  formatDoseAmountLabel,
  getDoseActionLabel,
  getScheduleMeta,
} from "./display-utils.js";

test("formats dose amount labels in English only", () => {
  assert.equal(formatDoseAmountLabel("1"), "1 tablet");
  assert.equal(formatDoseAmountLabel("2"), "2 tablets");
});

test("returns actionable label only for pending or late tasks", () => {
  assert.equal(canLogDose("PENDING"), true);
  assert.equal(canLogDose("LATE"), true);
  assert.equal(canLogDose("TAKEN"), false);
  assert.equal(getDoseActionLabel("PENDING"), "Log dose");
  assert.equal(getDoseActionLabel("LATE"), "Log as taken");
  assert.equal(getDoseActionLabel("TAKEN"), "Completed");
});

test("formats schedule meta without non-English text", () => {
  assert.equal(getScheduleMeta("DAILY", "1 tablet"), "DAILY - 1 tablet");
});
