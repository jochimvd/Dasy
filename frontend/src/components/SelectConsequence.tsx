import { For } from "solid-js";
import ConsequenceService from "../services/ConsequenceService";

type SelectProps = {
  value?: number;
  setConsequence: (id: number) => void;
};

const SelectConsequence = (props: SelectProps) => {
  const [data] = ConsequenceService().consequencesData();
  const consequences = () => data()?._embedded?.consequences;

  return (
    <>
      <label for="consequence" class="block text-sm font-medium text-gray-700">
        Consequence
      </label>
      <select
        id="consequence"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setConsequence(Number(e.currentTarget.value));
        }}
        required
      >
        <option value="">Select a consequence</option>
        <For each={consequences()}>
          {(consequence) => (
            <option
              value={consequence.id}
              selected={props.value === consequence.id}
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
