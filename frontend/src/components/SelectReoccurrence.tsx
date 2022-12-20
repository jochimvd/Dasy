import { For } from "solid-js";
import ReoccurrenceService from "../services/ReoccurrenceService";

type SelectProps = {
  value?: number;
  setReoccurrence: (id: number) => void;
};

const SelectReoccurrence = (props: SelectProps) => {
  const [data] = ReoccurrenceService().reoccurrencesData();
  const reoccurrences = () => data()?._embedded?.reoccurrences;

  return (
    <>
      <label for="reoccurrence" class="block text-sm font-medium text-gray-700">
        Reoccurrence rate
      </label>
      <select
        id="reoccurrence"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setReoccurrence(Number(e.currentTarget.value));
        }}
        required
      >
        <option>Select a reoccurrence rate</option>
        <For each={reoccurrences()}>
          {(reoccurrence) => (
            <option
              value={reoccurrence.id}
              selected={props.value === reoccurrence.id}
            >
              {reoccurrence.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectReoccurrence;
