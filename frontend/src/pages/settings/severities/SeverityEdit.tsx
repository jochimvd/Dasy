import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import { api, getUri } from "../../../utils";
import { fetchSeverity } from "./Severity";

export type SeverityInput = {
  name: string;
  level: number;
};

export const SeverityEdit = (props: { new?: boolean }) => {
  const emptySeverity: SeverityInput = {
    name: "",
    level: 0,
  };

  const putSeverity = async () => {
    return props.new
      ? await api.post("severities", { json: severity })
      : await api.put(new URL(data()?._links?.self.href ?? ""), {
          json: severity,
        });
  };

  const params = useParams();
  const navigate = useNavigate();

  const [data] = createResource(() => params.id, fetchSeverity);

  const [severity, setSeverity] = createStore(emptySeverity);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setSeverity({
      name: data()?.name ?? emptySeverity.name,
      level: data()?.level ?? emptySeverity.level,
    });
  });

  const submitSeverity = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putSeverity().then((res) => {
      if (res.ok) {
        navigate("/settings" + getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <h1 class="text-xl mb-5">Severity</h1>
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
            placeholder="Severity name"
            value={severity.name}
            onInput={(e) => {
              setSeverity({ name: e.currentTarget.value });
            }}
          />
        </div>
        <div class="mb-6">
          <label
            for="level"
            class="block mb-2 text-sm font-medium text-gray-900"
          >
            Level
          </label>
          <input
            id="level"
            type="number"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            placeholder="Severity level"
            value={severity.level}
            onInput={(e) => {
              setSeverity({
                level: e.currentTarget.valueAsNumber,
              });
            }}
          />
        </div>
        <button
          type="submit"
          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          onClick={submitSeverity}
        >
          {saving() ? "Saving..." : "Save"}
        </button>
      </form>
    </>
  );
};

export default SeverityEdit;
