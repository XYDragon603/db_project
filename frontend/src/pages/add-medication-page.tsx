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
  const [countries, setCountries] = useState<CatalogCountry[]>([]);
  const [catalogItems, setCatalogItems] = useState<MedicationCatalogItem[]>([]);
  const [countryCode, setCountryCode] = useState("");
  const [countryName, setCountryName] = useState("");
  const [catalogId, setCatalogId] = useState("");
  const [brandName, setBrandName] = useState("");
  const [medicineName, setMedicineName] = useState("");
  const [dosage, setDosage] = useState("");
  const [form, setForm] = useState("Tablet");
  const [isCatalogLoading, setIsCatalogLoading] = useState(false);

  useEffect(() => {
    void getCatalogCountries()
      .then((items) => {
        setCountries(items);
        setCountryCode(items[0]?.countryCode ?? "");
        setCountryName(items[0]?.countryName ?? "");
      })
      .catch(() => setError("Unable to load saved countries. You can still type one manually."));
  }, []);

  useEffect(() => {
    if (!countryCode) {
      setCatalogItems([]);
      setCatalogId("");
      return;
    }
    setIsCatalogLoading(true);
    setError(null);
    void getMedicationCatalog(countryCode)
      .then((items) => {
        setCatalogItems(items);
        const firstItem = items[0];
        setCatalogId(firstItem ? String(firstItem.catalogId) : "");
        setBrandName("");
        setMedicineName(firstItem?.genericName ?? "");
        setDosage(firstItem?.strength ?? "");
        setForm(firstItem?.dosageForm ?? "Tablet");
        if (items.length === 0) {
          setError("No medications are available for this country yet. You can enter one manually.");
        }
      })
      .catch(() => {
        setCatalogItems([]);
        setCatalogId("");
        setBrandName("");
        setError("Unable to load the medication catalog. You can still enter the medication manually.");
      })
      .finally(() => setIsCatalogLoading(false));
  }, [countryCode]);

  function changeCountry(value: string) {
    setCountryName(value);
    const selected = countries.find((country) =>
      country.countryName.toLowerCase() === value.trim().toLowerCase()
      || country.countryCode.toLowerCase() === value.trim().toLowerCase()
    );
    setCountryCode(selected?.countryCode ?? "");
    if (!selected) {
      setCatalogItems([]);
      setCatalogId("");
      setBrandName("");
    }
  }

  function changeMedication(value: string) {
    setMedicineName(value);
    const normalizedValue = value.trim().toLowerCase();
    const selected = catalogItems.find((item) =>
      item.genericName.toLowerCase() === normalizedValue
      || `${item.genericName} · ${item.strength}`.toLowerCase() === normalizedValue
    );
    if (selected) {
      setCatalogId(String(selected.catalogId));
      setMedicineName(selected.genericName);
      setDosage(selected.strength);
      setForm(selected.dosageForm);
    } else {
      setCatalogId("");
      setBrandName("");
    }
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
            </div>
          </div>
          <label className="space-y-2">
            <span className="text-sm font-medium text-slate-700">Country or region</span>
            <input className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" list="catalog-countries" value={countryName} onChange={(event) => changeCountry(event.target.value)} placeholder="Select or type a country" autoComplete="off" required />
            <datalist id="catalog-countries">
              {countries.map((country) => <option key={country.countryCode} value={country.countryName}>{country.countryCode}</option>)}
            </datalist>
            <span className="block text-xs text-slate-500">Choose a listed country to browse its catalog, or type another country.</span>
          </label>
          <label className="space-y-2">
            <span className="text-sm font-medium text-slate-700">Generic medication</span>
            <input className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" list="catalog-medications" value={medicineName} onChange={(event) => changeMedication(event.target.value)} placeholder={isCatalogLoading ? "Loading medications..." : "Select or type a medication"} autoComplete="off" required />
            <datalist id="catalog-medications">
              {catalogItems.map((item) => <option key={item.catalogId} value={`${item.genericName} · ${item.strength}`} />)}
            </datalist>
            <span className="block text-xs text-slate-500">Catalog matches fill the details automatically; new names remain editable.</span>
          </label>
              <label className="space-y-2 md:col-span-2">
                <span className="text-sm font-medium text-slate-700">Brand (optional)</span>
                <select className="flex h-12 w-full rounded-2xl border border-border bg-white px-4 text-sm" value={brandName} onChange={(event) => selectBrand(event.target.value)} disabled={!catalogId}>
                  <option value="">Use generic name</option>
                  {(catalogItems.find((item) => item.catalogId === Number(catalogId))?.brands ?? []).map((brand) => <option key={brand.brandId} value={brand.brandName}>{brand.brandName}{brand.manufacturer ? ` · ${brand.manufacturer}` : ""}</option>)}
                </select>
              </label>
              <input type="hidden" name="catalogId" value={catalogId} />
          <input type="hidden" name="medicineName" value={medicineName} />
          <FormInput label="Dosage" name="dosage" placeholder="500mg" value={dosage} onChange={(event) => setDosage(event.target.value)} required />
          <FormSelect label="Form" name="form" options={["Tablet", "Capsule", "Liquid"]} value={form} onChange={(event) => setForm(event.target.value)} required />
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
