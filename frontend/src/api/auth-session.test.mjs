import test from "node:test";
import assert from "node:assert/strict";
import { createAuthSession } from "./auth-session.js";

test("createAuthSession normalizes email and creates basic auth header", () => {
  const session = createAuthSession(
    {
      userId: 1,
      fullName: "Emily Johnson",
      email: "emily@example.com",
      role: "USER",
    },
    "  Emily@Example.com ",
    "password",
  );

  assert.equal(session.profile.email, "emily@example.com");
  assert.equal(session.authHeader, "Basic ZW1pbHlAZXhhbXBsZS5jb206cGFzc3dvcmQ=");
});
