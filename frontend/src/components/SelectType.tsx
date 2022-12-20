import { For } from "solid-js";
import TypeService from "../services/TypeService";

type SelectProps = {
  value?: number;
  setType: (id: number) => void;
  wrapperClass?: string;
};

const SelectType = (props: SelectProps) => {
  const [data] = TypeService().typesData();
  const types = () =>
    data()?._embedded?.types.sort((a, b) => a.name.localeCompare(b.name));

  return (
    <div class={props.wrapperClass}>
      <label for="type" class="block text-sm font-medium text-gray-700">
        Type
      </label>
      <select
        id="type"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setType(Number(e.currentTarget.value));
        }}
        required
      >
        <option>Select a type</option>
        <For each={types()}>
          {(type) => (
            <option value={type.id} selected={props.value === type.id}>
              {type.name}
            </option>
          )}
        </For>
      </select>
    </div>
  );
};

export default SelectType;
