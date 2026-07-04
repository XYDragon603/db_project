import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createAuthSession } from "@/api/auth-session";
import { register } from "@/api/client";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { MedMinderLogo } from "@/components/shared/medminder-logo";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import type { AuthSession } from "@/api/auth-session";

function resolveRegisterError(error: unknown) {
  if (error instanceof Error && error.message.includes("409")) {
    return "That email is already registered. Try signing in instead.";
  }

  return "Unable to create your account right now. Check the backend connection and try again.";
}

export function RegisterPage({ onRegistered }: { onRegistered: (session: AuthSession) => void }) {
  const navigate = useNavigate();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (password !== confirmPassword) {
      setError("Passwords must match before you continue.");
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const user = await register({
        fullName,
        email: email.trim().toLowerCase(),
        password,
        phone: phone.trim() || undefined,
      });
      onRegistered(createAuthSession(user, email, password));
      navigate("/user/dashboard");
    } catch (nextError) {
      setError(resolveRegisterError(nextError));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-8">
      <Card className="w-full max-w-xl space-y-6">
        <div>
          <div className="mb-4 flex items-center gap-3">
            <div className="rounded-2xl bg-white/80 p-1.5 shadow-sm ring-1 ring-sky-100">
              <MedMinderLogo className="h-12 w-12" />
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.24em] text-primary">MedMinder</p>
              <p className="text-sm text-slate-500">Medication reminder system</p>
            </div>
          </div>
          <h1 className="mt-3 text-4xl">Create your account</h1>
          <p className="mt-3">
            Start the user medication flow with a simple account setup designed for the course demo.
          </p>
        </div>
        <form className="grid gap-4 md:grid-cols-2" onSubmit={handleSubmit}>
          <div className="md:col-span-2">
            <FormInput
              label="Full name"
              name="fullName"
              placeholder="Emily Johnson"
              value={fullName}
              onChange={(event) => setFullName(event.target.value)}
              required
            />
          </div>
          <FormInput
            label="Email"
            name="email"
            placeholder="emily@example.com"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
          <FormInput
            label="Phone"
            name="phone"
            placeholder="555-0101"
            value={phone}
            onChange={(event) => setPhone(event.target.value)}
          />
          <FormInput
            label="Password"
            name="password"
            placeholder="Minimum 8 characters"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            minLength={8}
            required
          />
          <FormInput
            label="Confirm password"
            name="confirmPassword"
            placeholder="Repeat your password"
            type="password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            minLength={8}
            required
          />
          <div className="space-y-4 md:col-span-2">
            {error ? <FeedbackMessage message={error} variant="error" /> : null}
            <Button className="w-full" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Creating account..." : "Create account"}
            </Button>
            <p className="text-center text-sm text-slate-600">
              Already have an account?{" "}
              <Link to="/login" className="font-semibold text-primary hover:underline">
                Sign in
              </Link>
            </p>
          </div>
        </form>
      </Card>
    </div>
  );
}
