import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import { LocationInput } from "../../../models/Location";
import { api, getUri } from "../../../utils/utils";
import { fetchLocation } from "./Location";

export const LocationEdit = (props: { new?: boolean }) => {
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

  const [location, setLocation] = createStore({} as LocationInput);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setLocation({
      name: data()?.name ?? "",
    });
  });

  const submitLocation = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putLocation().then((res) => {
      if (res.ok) {
        navigate("/configuration" + getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <section aria-labelledby="location-heading">
        <form>
          <div class="shadow sm:rounded-md sm:overflow-hidden">
            <div class="bg-white py-6 px-4 sm:p-6">
              <div>
                <h2
                  id="location-heading"
                  class="text-lg leading-6 font-medium text-gray-900"
                >
                  Location
                </h2>
                <p class="mt-1 text-sm text-gray-500">
                  Create a new location or edit an existing one.
                </p>
              </div>

              <div class="mt-6 grid grid-cols-4 gap-6">
                <div class="col-span-4 sm:col-span-2">
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
                    placeholder="Location name"
                    value={location.name}
                    onInput={(e) => {
                      setLocation({ name: e.currentTarget.value });
                    }}
                  />
                </div>
              </div>
            </div>
            <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
              <button
                type="submit"
                class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
                onClick={submitLocation}
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

export default LocationEdit;
