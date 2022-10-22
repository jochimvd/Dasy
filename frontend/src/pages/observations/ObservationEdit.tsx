import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import { api, getUri } from "../../utils/utils";
import SelectCategory from "../../components/SelectCategory";
import { fetchObservation } from "./Observation";
import SelectLocation from "../../components/SelectLocation";
import { ObservationInput } from "../../models/Observation";

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

export const ObservationEdit = (props: { new?: boolean }) => {
  const putObservation = async () => {
    return props.new
      ? await api.post("observations", { json: observation })
      : await api.put(new URL(data()!._links!.self.href), {
          json: observation,
        });
  };

  const params = useParams();
  const navigate = useNavigate();

  const [data] = createResource(() => params.id, fetchObservation);

  const [observation, setObservation] = createStore({} as ObservationInput);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setObservation({
      observedAt: data()?.observedAt ?? toLocalIsoString(new Date()),
      location: { id: data()?.location.id },
      category: { id: data()?.category.id },
      observedCompany: data()?.observedCompany ?? "",
      immediateDanger: data()?.immediateDanger ?? false,
      description: data()?.description ?? "",
      actionsTaken: data()?.actionsTaken ?? "",
      furtherActions: data()?.furtherActions ?? "",
    });
  });

  const submitObservation = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putObservation().then((res) => {
      if (res.ok) {
        navigate(getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <form>
        <div class="shadow sm:rounded-md sm:overflow-hidden">
          <div class="bg-white py-6 px-4 sm:p-6">
            <div>
              <h2
                id="location-heading"
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
                  value={toLocalIsoString(
                    new Date(observation.observedAt!)
                  ).slice(0, 16)}
                  onInput={(e) => {
                    setObservation({
                      observedAt: toLocalIsoString(
                        new Date(e.currentTarget.value)
                      ),
                    });
                  }}
                />
              </div>

              <div class="col-span-4 sm:col-span-2">
                <label
                  for="observed-company"
                  class="block text-sm font-medium text-gray-700"
                >
                  Observed company
                </label>
                <input
                  type="text"
                  id="observed-company"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                  placeholder="Observed company"
                  value={observation.observedCompany}
                  onInput={(e) => {
                    setObservation({ observedCompany: e.currentTarget.value });
                  }}
                />
              </div>

              <div class="col-span-4 sm:col-span-2">
                <SelectLocation
                  value={observation.location.id}
                  setLocation={(id) => {
                    setObservation({ location: { id: id } });
                  }}
                />
              </div>

              <div class="col-span-4 sm:col-span-2">
                <SelectCategory
                  value={observation.category.id}
                  setCategory={(id) => {
                    setObservation({ category: { id: id } });
                  }}
                />
              </div>

              <div class="col-span-4 sm:col-span-2">
                <label for="immediate-danger">
                  <span class="block text-sm font-medium text-gray-700">
                    Immediate danger
                  </span>
                  <button
                    type="button"
                    id="immediate-danger"
                    classList={{
                      "bg-orange-500": observation.immediateDanger,
                      "bg-gray-200": !observation.immediateDanger,
                    }}
                    class="mt-1 relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900 transition-colors ease-in-out duration-200"
                    role="switch"
                    aria-checked={observation.immediateDanger}
                    aria-labelledby="immediate-danger"
                    onClick={() => {
                      setObservation({
                        immediateDanger: !observation.immediateDanger,
                      });
                    }}
                  >
                    <span
                      aria-hidden="true"
                      classList={{
                        "translate-x-5": observation.immediateDanger,
                        "translate-x-0": !observation.immediateDanger,
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
                  placeholder="Describe the observation"
                  value={observation.description}
                  onInput={(e) => {
                    setObservation({ description: e.currentTarget.value });
                  }}
                />
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
                  value={observation.actionsTaken}
                  onInput={(e) => {
                    setObservation({ actionsTaken: e.currentTarget.value });
                  }}
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
                  value={observation.furtherActions}
                  onInput={(e) => {
                    setObservation({ furtherActions: e.currentTarget.value });
                  }}
                />
              </div>
            </div>
          </div>
          <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
            <button
              type="submit"
              class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
              onClick={submitObservation}
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
