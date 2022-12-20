import { useNavigate, useRouteData } from "@solidjs/router";
import {
  Component,
  createComputed,
  createEffect,
  createSignal,
  Show,
} from "solid-js";
import { createStore, reconcile } from "solid-js/store";
import SuggestCompany from "../../components/SuggestCompany";
import SelectCategory from "../../components/SelectCategory";
import SelectSite from "../../components/SelectSite";
import { ObservationInput } from "../../models/Observation";
import ObservationService, {
  ObservationData,
} from "../../services/ObservationService";
import SelectType from "../../components/SelectType";
import SuggestSite from "../../components/SuggestSite";

function toLocalIsoString(date: Date) {
  const tzo = -date.getTimezoneOffset();
  const dif = tzo >= 0 ? "+" : "-";
  const pad = function (num: number) {
    return (num < 10 ? "0" : "") + num;
  };

  return (
    date.getFullYear() +
    "-" +
    pad(date.getMonth() + 1) +
    "-" +
    pad(date.getDate()) +
    "T" +
    pad(date.getHours()) +
    ":" +
    pad(date.getMinutes()) +
    ":" +
    pad(date.getSeconds()) +
    dif +
    pad(Math.floor(Math.abs(tzo) / 60)) +
    ":" +
    pad(Math.abs(tzo) % 60)
  );
}

export const ObservationEdit: Component = () => {
  const navigate = useNavigate();

  const observationService = ObservationService();
  const [data, { mutate }] = useRouteData<ObservationData>();
  const [saving, setSaving] = createSignal(false);
  const [errors, setErrors] = createStore({} as Record<string, string>);

  const [state, setState] = createStore<ObservationInput>({});
  const updateState = (field: keyof ObservationInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    setErrors(reconcile({}));

    if (!state.observedCompany) {
      setErrors({ observedCompany: "Please fill in the observed company." });
    }

    if (!state.category) {
      setErrors({ category: "Please fill in the category." });
    }

    if (!state.site) {
      setErrors({ site: "Please fill in the site." });
    }

    if (!state.description) {
      setErrors({ description: "Please fill in the description." });
    }

    if (Object.keys(errors).length > 0) {
      setSaving(false);
      return;
    }

    try {
      const observation = await (data()?.id
        ? observationService.updateObservation
        : observationService.createObservation)(state);

      mutate(observation);
      navigate("/observations/" + observation.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const observation = data();
    if (observation && observation.id) {
      const { _links, ...rest } = observation;
      setState(rest);
    }
  });

  createComputed(() => {
    // Initialize autofilled observedAt field
    if (state.observedAt === undefined) {
      setState("observedAt", toLocalIsoString(new Date()));
    }
    if (state.type === undefined) {
      setState("type", { id: 2 });
    }
    if (state.immediateDanger === undefined) {
      setState("immediateDanger", false);
    }
  });

  const [dangerEnabled, setDangerEnabled] = createSignal(true);

  createEffect(() => {
    // check for positive observation type
    if (state.type?.id === 1) {
      setDangerEnabled(false);
      setState("immediateDanger", false);
    } else {
      setDangerEnabled(true);
    }
  });

  return (
    <>
      <form autocomplete="off">
        <div class="shadow sm:rounded-md sm:overflow-hidden">
          <div class="bg-white py-6 px-4 sm:p-6">
            <div>
              <h2
                id="observation-heading"
                class="text-lg leading-6 font-medium text-gray-900"
              >
                Observation
              </h2>
              <p class="mt-1 text-sm text-gray-500">
                Create a new observation or edit an existing one.
              </p>
            </div>

            <div class="mt-6 grid grid-cols-4 gap-6">
              <div class="col-span-4 sm:col-span-2">
                <label
                  for="observed-at"
                  class="block text-sm font-medium text-gray-700"
                >
                  Time of observation
                </label>
                <input
                  type="datetime-local"
                  id="observed-at"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                  max={toLocalIsoString(new Date()).slice(0, 16)}
                  value={toLocalIsoString(new Date(state.observedAt!)).slice(
                    0,
                    16
                  )}
                  onInput={(e) => {
                    setState(
                      "observedAt",
                      toLocalIsoString(
                        e.currentTarget.value
                          ? new Date(e.currentTarget.value)
                          : new Date()
                      )
                    );
                  }}
                />
              </div>

              <SuggestCompany
                id="observedCompany"
                wrapperClass="col-span-4 sm:col-span-2"
                value={state.observedCompany}
                setValue={(value) => {
                  setErrors({ observedCompany: undefined });
                  setState("observedCompany", value);
                }}
                error={errors.observedCompany}
              />

              <SelectCategory
                value={state.category?.id}
                wrapperClass="col-span-4 sm:col-span-2"
                setCategory={(id) => {
                  setErrors({ category: undefined });
                  Number.isNaN(id)
                    ? setState("category", undefined)
                    : setState("category", { id: id });
                }}
                error={errors.category}
              />

              <SuggestSite
                id="site"
                wrapperClass="col-span-4 sm:col-span-2"
                value={state.site}
                setValue={(value) => {
                  setErrors({ site: undefined });
                  setState("site", value);
                }}
                error={errors.site}
              />

              <SelectType
                wrapperClass="col-span-4 sm:col-span-2"
                value={state.type?.id}
                setType={(id) => {
                  setErrors({ type: undefined });
                  Number.isNaN(id)
                    ? setState("type", undefined)
                    : setState("type", { id: id });
                }}
              />

              <div class="col-span-4 sm:col-span-2">
                <label for="immediate-danger">
                  <span class="block text-sm font-medium text-gray-700">
                    Immediate danger
                  </span>
                  <button
                    type="button"
                    id="immediate-danger"
                    classList={{
                      "bg-orange-500": state.immediateDanger,
                      "bg-gray-200": !state.immediateDanger,
                      "hover:cursor-not-allowed": !dangerEnabled(),
                    }}
                    class="mt-3 relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900 transition-colors ease-in-out duration-200"
                    role="switch"
                    aria-checked={state.immediateDanger}
                    aria-labelledby="immediate-danger"
                    onClick={() =>
                      setState("immediateDanger", !state.immediateDanger)
                    }
                    disabled={!dangerEnabled()}
                  >
                    <span
                      aria-hidden="true"
                      classList={{
                        "translate-x-5": state.immediateDanger,
                        "translate-x-0": !state.immediateDanger,
                      }}
                      class="inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200"
                    ></span>
                  </button>
                </label>
              </div>
            </div>
            <div class="mt-6 grid grid-cols-4 gap-6">
              <div class="col-span-4">
                <label
                  for="description"
                  class="block text-sm font-medium text-gray-700"
                >
                  Description
                </label>
                <textarea
                  rows={3}
                  id="description"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                  classList={{
                    "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500":
                      !!errors.description,
                  }}
                  placeholder="Describe the observation"
                  value={state.description ?? ""}
                  onInput={(e) => {
                    setErrors({ description: undefined });
                    updateState("description")(e);
                  }}
                />
                <Show when={errors.description}>
                  <p class="mt-2 text-sm text-red-600">{errors.description}</p>
                </Show>
              </div>

              <div class="col-span-4">
                <label
                  for="actions-taken"
                  class="block text-sm font-medium text-gray-700"
                >
                  Actions taken
                </label>
                <textarea
                  rows={2}
                  id="actions-taken"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                  placeholder="Describe what actions were taken"
                  value={state.actionsTaken ?? ""}
                  onInput={updateState("actionsTaken")}
                />
              </div>

              <div class="col-span-4">
                <label
                  for="further-actions"
                  class="block text-sm font-medium text-gray-700"
                >
                  Further actions
                </label>
                <textarea
                  rows={2}
                  id="further-actions"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                  placeholder="Describe any further actions to be taken"
                  value={state.furtherActions ?? ""}
                  onInput={updateState("furtherActions")}
                />
              </div>
            </div>
          </div>
          <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
            <button
              type="submit"
              class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
              onClick={submitForm}
            >
              {saving() ? "Saving..." : "Save"}
            </button>
          </div>
        </div>
      </form>
    </>
  );
};

export default ObservationEdit;
