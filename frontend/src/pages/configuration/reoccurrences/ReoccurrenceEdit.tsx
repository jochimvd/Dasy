import { useNavigate, useRouteData } from "@solidjs/router";
import { createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { ReoccurrenceInput } from "../../../models/Reoccurrence";
import ReoccurrenceService, {
  ReoccurrenceData,
} from "../../../services/ReoccurrenceService";

const ReoccurrenceEdit = () => {
  const navigate = useNavigate();

  const reoccurrenceService = ReoccurrenceService();
  const [data, { mutate }] = useRouteData<ReoccurrenceData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<ReoccurrenceInput>({});
  const updateState = (field: keyof ReoccurrenceInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const reoccurrence = await (data()?.id
        ? reoccurrenceService.updateReoccurrence
        : reoccurrenceService.createReoccurrence)(state);

      mutate(reoccurrence);
      navigate("/configuration/reoccurrences/" + reoccurrence.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const reoccurrence = data();
    if (reoccurrence && reoccurrence.id) {
      const { _links, ...rest } = reoccurrence;
      setState(rest);
    }
  });

  return (
    <ConfigurationEditLayout
      heading={"Reoccurrence"}
      subheading={"Create a new reoccurrence rate or edit an existing one."}
      saving={saving}
      onSubmit={submitForm}
    >
      <div class="mt-6 grid grid-cols-4 gap-6">
        <div class="col-span-4 sm:col-span-3">
          <label for="name" class="block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            id="name"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            placeholder="Reoccurrence name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>

        <div class="col-span-4 sm:col-span-1">
          <label for="rate" class="block text-sm font-medium text-gray-700">
            Rate
          </label>
          <input
            type="number"
            id="rate"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            value={state.rate}
            onInput={(e) => setState("rate", e.currentTarget.valueAsNumber)}
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default ReoccurrenceEdit;
