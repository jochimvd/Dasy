import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import { api, getUri } from "../../../utils";
import { fetchConsequence } from "./Consequence";

export type ConsequenceInput = {
  name: string;
  probability: number;
};

export const ConsequenceEdit = (props: { new?: boolean }) => {
  const emptyConsequence: ConsequenceInput = {
    name: "",
    probability: 0,
  };

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

  const [consequence, setConsequence] = createStore(emptyConsequence);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setConsequence({
      name: data()?.name ?? emptyConsequence.name,
      probability: data()?.probability ?? emptyConsequence.probability,
    });
  });

  const submitConsequence = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putConsequence().then((res) => {
      if (res.ok) {
        navigate("/settings" + getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <h1 class="text-xl mb-5">Consequence</h1>
      <form>
        <div class="mb-6">
          <label
            for="name"
            class="block mb-2 text-sm font-medium text-gray-900"
          >
            Name
          </label>
          <input
            id="name"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            placeholder="Consequence name"
            value={consequence.name}
            onInput={(e) => {
              setConsequence({ name: e.currentTarget.value });
            }}
          />
        </div>
        <div class="mb-6">
          <label
            for="probability"
            class="block mb-2 text-sm font-medium text-gray-900"
          >
            Probability
          </label>
          <input
            id="probability"
            type="number"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            placeholder="Consequence probability"
            value={consequence.probability}
            onInput={(e) => {
              setConsequence({
                probability: e.currentTarget.valueAsNumber,
              });
            }}
          />
        </div>
        <button
          type="submit"
          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          onClick={submitConsequence}
        >
          {saving() ? "Saving..." : "Save"}
        </button>
      </form>
    </>
  );
};

export default ConsequenceEdit;
