import { useEffect, useState } from "react";
import { getMyProfile, updateMyProfile } from "@/api/client";
import { updateAuthSessionProfile } from "@/api/auth-session";
import type { LoginResponse, UserProfile } from "@/api/types";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { LoadingState } from "@/components/shared/loading-state";
import { PageHeader } from "@/components/shared/page-header";
import { SectionCard } from "@/components/shared/section-card";
import { Button } from "@/components/ui/button";
import { formatDisplayDateTime } from "@/lib/date-display";
import { UserLayout } from "@/layouts/user-layout";

function resolveLoadError(error: unknown) {
  if (error instanceof Error && error.message.includes("403")) {
    return "You can only view your own profile.";
  }

  return "Unable to load your profile right now.";
}

function resolveSaveError(error: unknown) {
  if (error instanceof Error && error.message.includes("400")) {
    return "Enter a full name before saving your profile.";
  }
  if (error instanceof Error && error.message.includes("403")) {
    return "You can only update your own profile.";
  }

  return "Unable to save your profile right now.";
}

export function ProfilePage({ user }: { user: LoginResponse }) {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [fullName, setFullName] = useState("");
  const [phone, setPhone] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function loadProfile() {
      setIsLoading(true);
      setError(null);
      setMessage(null);
      try {
        const next = await getMyProfile(user.userId);
        if (isMounted) {
          setProfile(next);
          setFullName(next.fullName);
          setPhone(next.phone ?? "");
        }
      } catch (nextError) {
        if (isMounted) {
          setError(resolveLoadError(nextError));
          setProfile(null);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadProfile();

    return () => {
      isMounted = false;
    };
  }, [user.userId]);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setIsSaving(true);
    setError(null);
    setMessage(null);
    try {
      const updated = await updateMyProfile(user.userId, {
        fullName,
        phone,
      });
      updateAuthSessionProfile({
        fullName: updated.fullName,
        email: updated.email,
      });
      setProfile(updated);
      setFullName(updated.fullName);
      setPhone(updated.phone ?? "");
      setMessage("Your profile was updated successfully.");
    } catch (nextError) {
      setError(resolveSaveError(nextError));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <UserLayout title="Profile / Settings">
      <PageHeader
        eyebrow="Profile / Settings"
        title="Manage your basic account details"
        description="Update the personal details stored in your user record without changing roles, account status, or authentication settings."
      />
      {isLoading ? (
        <LoadingState label="Loading your profile..." />
      ) : profile ? (
        <div className="grid gap-5 xl:grid-cols-[minmax(0,1.15fr)_minmax(320px,0.85fr)]">
          <SectionCard
            title="Basic profile"
            description="Keep this information current so your medication records and caregiver access views stay accurate."
          >
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
                value={profile.email}
                readOnly
                disabled
              />
              <FormInput
                label="Phone"
                name="phone"
                placeholder="555-0101"
                value={phone}
                onChange={(event) => setPhone(event.target.value)}
              />
              <div className="md:col-span-2 flex flex-wrap items-center gap-3">
                <Button type="submit" disabled={isSaving}>
                  {isSaving ? "Saving..." : "Save profile"}
                </Button>
              </div>
              <div className="md:col-span-2 space-y-3">
                {message ? <FeedbackMessage message={message} variant="success" /> : null}
                {error ? <FeedbackMessage message={error} variant="error" /> : null}
              </div>
            </form>
          </SectionCard>

          <SectionCard
            title="Account details"
            description="These account fields are read-only in the minimum closed-loop version."
          >
            <div className="space-y-4 text-sm text-slate-600">
              <div>
                <p className="font-medium text-slate-900">Account status</p>
                <p>{profile.active ? "Active" : "Inactive"}</p>
              </div>
              <div>
                <p className="font-medium text-slate-900">Created date</p>
                <p>{formatDisplayDateTime(profile.createdAt)}</p>
              </div>
              <div>
                <p className="font-medium text-slate-900">Role access</p>
                <p>User role and permissions are managed outside this page.</p>
              </div>
            </div>
          </SectionCard>
        </div>
      ) : (
        <SectionCard title="Profile unavailable" description="We could not load your account details.">
          {error ? <FeedbackMessage message={error} variant="error" /> : null}
        </SectionCard>
      )}
    </UserLayout>
  );
}
