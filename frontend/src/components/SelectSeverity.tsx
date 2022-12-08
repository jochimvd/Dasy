import { For } from "solid-js";
import SeverityService from "../services/SeverityService";

type SelectProps = {
  value?: number;
  setSeverity: (id: number) => void;
};

const SelectSeverity = (props: SelectProps) => {
  const [data] = SeverityService().severitiesData();
  const severities = () => data()?._embedded?.severities;

  return (
    <>
      <label for="severity" class="block text-sm font-medium text-gray-700">
        Severity
      </label>
      <select
        id="severity"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setSeverity(Number(e.currentTarget.value));
        }}
        required
      >
        <option>Select a severity</option>
        <For each={severities()}>
          {(severity) => (
            <option value={severity.id} selected={props.value === severity.id}>
              {severity.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectSeverity;
