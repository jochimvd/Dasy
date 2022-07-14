import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import SelectConsequence from "../../../components/SelectConsequence";
import SelectSeverity from "../../../components/SelectSeverity";
import { api, getUri } from "../../../utils";
import { fetchLocation } from "./Location";

export type LocationInput = {
  name: string;
};

export const LocationEdit = (props: { new?: boolean }) => {
  const emptyLocation: LocationInput = {
    name: "",
  };

  const putLocation = async () => {
    return props.new
      ? await api.post("locations", { json: location })
      : await api.put(new URL(data()?._links?.self.href ?? ""), {
          json: location,
        });
  };

  const params = useParams();
  const navigate = useNavigate();

  const [data] = createResource(() => params.id, fetchLocation);

  const [location, setLocation] = createStore(emptyLocation);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setLocation({
      name: data()?.name ?? emptyLocation.name,
    });
  });

  const submitLocation = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putLocation().then((res) => {
      if (res.ok) {
        navigate("/settings" + getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <h1 class="text-xl mb-5">Location</h1>
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
            placeholder="Location name"
            value={location.name}
            onInput={(e) => {
              setLocation({ name: e.currentTarget.value });
            }}
          />
        </div>
        <button
          type="submit"
          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          onClick={submitLocation}
        >
          {saving() ? "Saving..." : "Save"}
        </button>
      </form>
    </>
  );
};

export default LocationEdit;
