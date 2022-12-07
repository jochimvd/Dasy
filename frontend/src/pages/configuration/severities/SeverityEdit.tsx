import { useNavigate, useRouteData } from "@solidjs/router";
import { createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { SeverityInput } from "../../../models/Severity";
import SeverityService, {
  SeverityData,
} from "../../../services/SeverityService";

export const SeverityEdit = () => {
  const navigate = useNavigate();

  const severitiyService = SeverityService();
  const [data, { mutate }] = useRouteData<SeverityData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<SeverityInput>({});
  const updateState = (field: keyof SeverityInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const severitiy = await (data()?.id
        ? severitiyService.updateSeverity
        : severitiyService.createSeverity)(state);

      mutate(severitiy);
      navigate("/configuration/severities/" + severitiy.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const severitiy = data();
    if (severitiy && severitiy.id) {
      const { _links, ...rest } = severitiy;
      setState(rest);
    }
  });

  return (
    <ConfigurationEditLayout
      heading={"Severity"}
      subheading={"Create a new severity level or edit an existing one."}
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
            placeholder="Severity name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>

        <div class="col-span-4 sm:col-span-1">
          <label for="level" class="block text-sm font-medium text-gray-700">
            Level
          </label>
          <input
            type="number"
            id="level"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            value={state.level}
            onInput={(e) => setState("level", e.currentTarget.valueAsNumber)}
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default SeverityEdit;
