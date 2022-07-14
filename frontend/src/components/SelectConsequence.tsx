import { createResource, For } from "solid-js";
import { fetchConsequences } from "../pages/settings/consequences/Consequences";

type SelectProps = {
  value: string;
  setConsequence: (url: string) => void;
};

const SelectConsequence = (props: SelectProps) => {
  const [consequences] = createResource(fetchConsequences);

  return (
    <>
      <label
        for="consequence"
        class="block mb-2 text-sm font-medium text-gray-900"
      >
        Consequence
      </label>
      <select
        id="consequence"
        class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
        onInput={(e) => {
          props.setConsequence(e.currentTarget.value);
        }}
        required
      >
        <option value="">Select a consequence</option>
        <For each={consequences()}>
          {(consequence) => (
            <option
              value={consequence._links?.self.href}
              selected={props.value === consequence._links?.self.href}
            >
              {consequence.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectConsequence;
