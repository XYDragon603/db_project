import { useState } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { createAuthSession } from "@/api/auth-session";
import { MedMinderLogo } from "@/components/shared/medminder-logo";
import { login } from "@/api/client";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { FormInput } from "@/components/shared/form-input";
import type { AuthSession } from "@/api/auth-session";

export function LoginPage({ onLoggedIn }: { onLoggedIn: (session: AuthSession) => void }) {
  const navigate = useNavigate();
  const [email, setEmail] = useState("emily@example.com");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setIsSubmitting(true);
    setError(null);
    try {
      const user = await login(email, password);
      if (!user) {
        throw new Error("Invalid demo login");
      }
      onLoggedIn(createAuthSession(user, email, password));
      if (user.role === "USER") {
        navigate("/user/dashboard");
      } else if (user.role === "CAREGIVER") {
        navigate("/caregiver/dashboard");
      } else {
        navigate("/admin/audit-logs");
      }
    } catch {
      setError("Unable to sign in. Check the demo account details or backend connection.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-8">
      <Card className="w-full max-w-md space-y-6">
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
          <h1 className="mt-3 text-4xl">Welcome back</h1>
          <p className="mt-3">Use one of the seeded demo accounts to enter the User, Caregiver, or Admin flow.</p>
        </div>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <label className="space-y-2">
            <span className="text-sm font-medium text-slate-700">Email</span>
            <input className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" value={email} onChange={(e) => setEmail(e.target.value)} />
          </label>
          <label className="space-y-2">
            <span className="text-sm font-medium text-slate-700">Password</span>
            <input className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </label>
          {error ? <FeedbackMessage message={error} variant="error" /> : null}
          <Button className="w-full" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Signing in..." : "Sign in"}
          </Button>
        </form>
        <div className="rounded-2xl bg-secondary p-4 text-sm text-slate-600">
          <div><strong>User:</strong> emily@example.com</div>
          <div><strong>Caregiver:</strong> alex.caregiver@example.com</div>
          <div><strong>Admin:</strong> admin@example.com</div>
          <div><strong>Password:</strong> password</div>
        </div>
        <p className="text-center text-sm text-slate-600">
          Need a new user account?{" "}
          <Link to="/register" className="font-semibold text-primary hover:underline">
            Register here
          </Link>
        </p>
      </Card>
    </div>
  );
}
