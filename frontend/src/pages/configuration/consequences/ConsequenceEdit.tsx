import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import { ConsequenceInput } from "../../../models/Consequence";
import { api, getUri } from "../../../utils/utils";
import { fetchConsequence } from "./Consequence";

export const ConsequenceEdit = (props: { new?: boolean }) => {
  const putConsequence = async () => {
    return props.new
      ? await api.post("consequences", { json: consequence })
      : await api.put(new URL(data()?._links?.self.href ?? ""), {
          json: consequence,
        });
  };

  const params = useParams();
  const navigate = useNavigate();

  const [data] = createResource(() => params.id, fetchConsequence);

  const [consequence, setConsequence] = createStore({} as ConsequenceInput);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setConsequence({
      name: data()?.name ?? "",
      probability: data()?.probability,
    });
  });

  const submitConsequence = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putConsequence().then((res) => {
      if (res.ok) {
        navigate("/configuration" + getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <section aria-labelledby="consequence-heading">
        <form>
          <div class="shadow sm:rounded-md sm:overflow-hidden">
            <div class="bg-white py-6 px-4 sm:p-6">
              <div>
                <h2
                  id="consequence-heading"
                  class="text-lg leading-6 font-medium text-gray-900"
                >
                  Consequence
                </h2>
                <p class="mt-1 text-sm text-gray-500">
                  Create a new consequence probability or edit an existing one.
                </p>
              </div>

              <div class="mt-6 grid grid-cols-4 gap-6">
                <div class="col-span-4 sm:col-span-3">
                  <label
                    for="name"
                    class="block text-sm font-medium text-gray-700"
                  >
                    Name
                  </label>
                  <input
                    type="text"
                    id="name"
                    class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                    placeholder="Consequence name"
                    value={consequence.name}
                    onInput={(e) => {
                      setConsequence({ name: e.currentTarget.value });
                    }}
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
                    value={consequence.probability}
                    onInput={(e) => {
                      setConsequence({
                        probability: e.currentTarget.valueAsNumber,
                      });
                    }}
                  />
                </div>
              </div>
            </div>
            <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
              <button
                type="submit"
                class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
                onClick={submitConsequence}
              >
                {saving() ? "Saving..." : "Save"}
              </button>
            </div>
          </div>
        </form>
      </section>
    </>
  );
};

export default ConsequenceEdit;
