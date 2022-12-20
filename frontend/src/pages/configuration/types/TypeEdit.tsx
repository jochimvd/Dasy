import { useNavigate, useRouteData } from "@solidjs/router";
import { createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { TypeInput } from "../../../models/Type";
import TypeService, { TypeData } from "../../../services/TypeService";

const TypeEdit = () => {
  const navigate = useNavigate();

  const typeService = TypeService();
  const [data, { mutate }] = useRouteData<TypeData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<TypeInput>({});
  const updateState = (field: keyof TypeInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const type = await (data()?.id
        ? typeService.updateType
        : typeService.createType)(state);

      mutate(type);
      navigate("/configuration/types/" + type.id, { replace: true });
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const type = data();
    if (type && type.id) {
      const { _links, ...rest } = type;
      setState(rest);
    }
  });

  return (
    <ConfigurationEditLayout
      heading={"Type"}
      subheading={"Create a new type rate or edit an existing one."}
      saving={saving}
      onSubmit={submitForm}
    >
      <div class="mt-6 grid grid-cols-4 gap-6">
        <div class="col-span-4 sm:col-span-2">
          <label for="name" class="block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            id="name"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            placeholder="Type name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>

        <div class="col-span-4 sm:col-start-1 sm:col-span-1">
          <label for="notify">
            <span class="block text-sm font-medium text-gray-700">Notify</span>

            <button
              type="button"
              id="notify"
              classList={{
                "bg-orange-500": state.notify,
                "bg-gray-200": !state.notify,
              }}
              class="mt-1 relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900 transition-colors ease-in-out duration-200"
              role="switch"
              aria-checked={state.notify}
              aria-labelledby="notify"
              onClick={() => setState("notify", !state.notify)}
            >
              <span
                aria-hidden="true"
                classList={{
                  "translate-x-5": state.notify,
                  "translate-x-0": !state.notify,
                }}
                class="inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200"
              ></span>
            </button>
          </label>
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default TypeEdit;
