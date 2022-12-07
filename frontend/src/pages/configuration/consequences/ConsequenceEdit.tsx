import { useNavigate, useRouteData } from "@solidjs/router";
import { createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { ConsequenceInput } from "../../../models/Consequence";
import ConsequenceService, {
  ConsequenceData,
} from "../../../services/ConsequenceService";

const ConsequenceEdit = () => {
  const navigate = useNavigate();

  const consequenceService = ConsequenceService();
  const [data, { mutate }] = useRouteData<ConsequenceData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<ConsequenceInput>({});
  const updateState = (field: keyof ConsequenceInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const consequence = await (data()?.id
        ? consequenceService.updateConsequence
        : consequenceService.createConsequence)(state);

      mutate(consequence);
      navigate("/configuration/consequences/" + consequence.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const consequence = data();
    if (consequence && consequence.id) {
      const { _links, ...rest } = consequence;
      setState(rest);
    }
  });

  return (
    <ConfigurationEditLayout
      heading={"Consequence"}
      subheading={
        "Create a new consequence probability or edit an existing one."
      }
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
            placeholder="Consequence name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>

        <div class="col-span-4 sm:col-span-1">
          <label
            for="probability"
            class="block text-sm font-medium text-gray-700"
          >
            Probability
          </label>
          <input
            type="number"
            id="probability"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            value={state.probability}
            onInput={(e) =>
              setState("probability", e.currentTarget.valueAsNumber)
            }
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default ConsequenceEdit;
