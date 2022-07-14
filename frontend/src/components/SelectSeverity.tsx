import { createResource, For } from "solid-js";
import { fetchSeverities } from "../pages/settings/severities/Severities";

type SelectProps = {
  value: string;
  setSeverity: (url: string) => void;
};

const SelectSeverity = (props: SelectProps) => {
  const [severities] = createResource(fetchSeverities);

  return (
    <>
      <label
        for="severity"
        class="block mb-2 text-sm font-medium text-gray-900"
      >
        Severity
      </label>
      <select
        id="severity"
        class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
        onInput={(e) => {
          props.setSeverity(e.currentTarget.value);
        }}
        required
      >
        <option value="">Select a severity</option>
        <For each={severities()}>
          {(severity) => (
            <option
              value={severity._links?.self.href}
              selected={props.value === severity._links?.self.href}
            >
              {severity.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectSeverity;
