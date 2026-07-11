import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { createMedication, getCatalogCountries, getMedicationCatalog } from "@/api/client";
import type { CatalogCountry, LoginResponse, MedicationCatalogItem } from "@/api/types";
import { DateInput } from "@/components/shared/date-input";
import { FeedbackMessage } from "@/components/shared/feedback-message";
import { FormInput } from "@/components/shared/form-input";
import { FormSelect } from "@/components/shared/form-select";
import { PageHeader } from "@/components/shared/page-header";
import { Button } from "@/components/ui/button";
import { SectionCard } from "@/components/shared/section-card";
import { UserLayout } from "@/layouts/user-layout";

function resolveMedicationSaveError(error: unknown) {
  if (error instanceof Error && error.message.includes("400")) {
    return "Complete the required medication fields before saving.";
  }
  if (error instanceof Error && error.message.includes("403")) {
    return "You can only create medications for your own account.";
  }

  return "Unable to save this medication right now.";
}

export function AddMedicationPage({ user }: { user: LoginResponse }) {
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  const [manualEntry, setManualEntry] = useState(false);
  const [countries, setCountries] = useState<CatalogCountry[]>([]);
  const [catalogItems, setCatalogItems] = useState<MedicationCatalogItem[]>([]);
  const [countryCode, setCountryCode] = useState("");
  const [catalogId, setCatalogId] = useState("");
  const [brandName, setBrandName] = useState("");
  const [medicineName, setMedicineName] = useState("");
  const [dosage, setDosage] = useState("");
  const [form, setForm] = useState("Tablet");

  useEffect(() => {
    void getCatalogCountries()
      .then((items) => {
        setCountries(items);
        setCountryCode(items[0]?.countryCode ?? "");
      })
      .catch(() => setManualEntry(true));
  }, []);

  useEffect(() => {
    if (!countryCode || manualEntry) {
      return;
    }
    void getMedicationCatalog(countryCode)
      .then((items) => {
        setCatalogItems(items);
        setCatalogId("");
        setBrandName("");
      })
      .catch(() => setError("Unable to load the medication catalog. You can still enter the medication manually."));
  }, [countryCode, manualEntry]);

  function selectCatalogItem(value: string) {
    setCatalogId(value);
    const selected = catalogItems.find((item) => item.catalogId === Number(value));
    if (!selected) {
      return;
    }
    setMedicineName(selected.genericName);
    setDosage(selected.strength);
    setForm(selected.dosageForm);
    setBrandName("");
  }

  function selectBrand(value: string) {
    setBrandName(value);
    if (value) {
      setMedicineName(value);
    } else {
      const selected = catalogItems.find((item) => item.catalogId === Number(catalogId));
      setMedicineName(selected?.genericName ?? "");
    }
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const formElement = event.currentTarget;
    const form = new FormData(formElement);
    const submittedMedicineName = String(form.get("medicineName") ?? "Medication");

    setIsSaving(true);
    setMessage(null);
    setError(null);
    try {
      await createMedication(user.userId, Object.fromEntries(form.entries()));
      setError(null);
      setMessage(`${submittedMedicineName} was added successfully. You can now add a daily schedule.`);
      formElement.reset();
      setCatalogId("");
      setBrandName("");
      setMedicineName("");
      setDosage("");
      setForm("Tablet");
    } catch (nextError) {
      setError(resolveMedicationSaveError(nextError));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <UserLayout title="Add Medication">
      <PageHeader
        eyebrow="Add Medication"
        title="Create a medication record"
        description="Choose a localized catalog entry or enter medication details manually."
      />
      <SectionCard title="Medication details">
        <form className="grid gap-4 md:grid-cols-2" onSubmit={handleSubmit}>
          <div className="md:col-span-2 rounded-3xl border border-blue-100 bg-blue-50/70 p-4">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <div>
                <p className="font-semibold text-slate-900">Medication identification</p>
                <p className="mt-1 text-sm text-slate-600">Catalog information varies by country and is not medical advice.</p>
              </div>
              <Button type="button" variant="secondary" onClick={() => {
                setManualEntry((current) => !current);
                setCatalogId("");
                setMedicineName("");
                setDosage("");
                setForm("Tablet");
              }}>
                {manualEntry ? "Browse local catalog" : "Enter manually"}
              </Button>
            </div>
          </div>
          {!manualEntry ? (
            <>
              <label className="space-y-2">
                <span className="text-sm font-medium text-slate-700">Country or region</span>
                <select className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" value={countryCode} onChange={(event) => setCountryCode(event.target.value)}>
                  {countries.map((country) => <option key={country.countryCode} value={country.countryCode}>{country.countryName}</option>)}
                </select>
              </label>
              <label className="space-y-2">
                <span className="text-sm font-medium text-slate-700">Generic medication</span>
                <select className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" value={catalogId} onChange={(event) => selectCatalogItem(event.target.value)} required>
                  <option value="">Select a medication</option>
                  {catalogItems.map((item) => <option key={item.catalogId} value={item.catalogId}>{item.genericName} · {item.strength}</option>)}
                </select>
              </label>
              <label className="space-y-2 md:col-span-2">
                <span className="text-sm font-medium text-slate-700">Brand (optional)</span>
                <select className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" value={brandName} onChange={(event) => selectBrand(event.target.value)} disabled={!catalogId}>
                  <option value="">Use generic name</option>
                  {(catalogItems.find((item) => item.catalogId === Number(catalogId))?.brands ?? []).map((brand) => <option key={brand.brandId} value={brand.brandName}>{brand.brandName}{brand.manufacturer ? ` · ${brand.manufacturer}` : ""}</option>)}
                </select>
              </label>
              <input type="hidden" name="catalogId" value={catalogId} />
            </>
          ) : null}
          <FormInput label="Medicine name" name="medicineName" placeholder="Metformin" value={medicineName} onChange={(event) => setMedicineName(event.target.value)} readOnly={!manualEntry} required />
          <FormInput label="Dosage" name="dosage" placeholder="500mg" value={dosage} onChange={(event) => setDosage(event.target.value)} readOnly={!manualEntry} required />
          <FormSelect label="Form" name="form" options={["Tablet", "Capsule", "Liquid"]} value={form} onChange={(event) => setForm(event.target.value)} disabled={!manualEntry} required />
          {!manualEntry ? <input type="hidden" name="form" value={form} /> : null}
          <FormInput label="Current quantity" name="currentQuantity" placeholder="30" type="number" min="0" defaultValue="30" required />
          <FormInput label="Refill threshold" name="refillThreshold" placeholder="5" type="number" min="0" defaultValue="5" required />
          <DateInput label="Start date" name="startDate" />
          <div className="md:col-span-2">
            <FormInput label="Notes" name="notes" placeholder="Take after lunch" />
          </div>
          <div className="md:col-span-2 flex flex-wrap items-center gap-3">
            <Button type="submit" disabled={isSaving}>
              {isSaving ? "Saving..." : "Save medication"}
            </Button>
            <Link to="/user/schedules">
              <Button type="button" variant="secondary">Go to schedules</Button>
            </Link>
          </div>
          <div className="md:col-span-2">
            {message ? <FeedbackMessage message={message} variant="success" /> : null}
            {error ? <FeedbackMessage message={error} variant="error" /> : null}
          </div>
        </form>
      </SectionCard>
    </UserLayout>
  );
}
