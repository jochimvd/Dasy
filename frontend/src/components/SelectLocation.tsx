import { createResource, For } from "solid-js";
import { fetchLocations } from "../pages/configuration/locations/Locations";

type SelectProps = {
  value?: number;
  setLocation: (id: number) => void;
};

const SelectLocation = (props: SelectProps) => {
  const [locations] = createResource(fetchLocations);

  return (
    <>
      <label for="location" class="block text-sm font-medium text-gray-700">
        Location
      </label>
      <select
        id="location"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setLocation(Number(e.currentTarget.value));
        }}
        required
      >
        <option value="">Select a location</option>
        <For each={locations()?.sort((a, b) => a.name.localeCompare(b.name))}>
          {(location) => (
            <option value={location.id} selected={props.value === location.id}>
              {location.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectLocation;
