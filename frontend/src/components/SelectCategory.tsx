import { For, Show } from "solid-js";
import CategoryService from "../services/CategoryService";

type SelectProps = {
  value?: number;
  setCategory: (id: number) => void;
  wrapperClass?: string;
  error?: string;
};

const SelectCategory = (props: SelectProps) => {
  const [data] = CategoryService().categoriesData();
  const categories = () =>
    data()?._embedded?.categories.sort((a, b) => a.name.localeCompare(b.name));

  return (
    <div class={props.wrapperClass}>
      <label for="category" class="block text-sm font-medium text-gray-700">
        Category
      </label>
      <select
        id="category"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        classList={{
          "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500":
            !!props.error,
        }}
        onInput={(e) => {
          console.log(e.currentTarget.value);
          props.setCategory(Number(e.currentTarget.value));
        }}
        required
      >
        <option>Select a category</option>
        <For each={categories()}>
          {(category) => (
            <option value={category.id} selected={props.value === category.id}>
              {category.name}
            </option>
          )}
        </For>
      </select>
      <Show when={props.error}>
        <p class="mt-2 text-sm text-red-600">{props.error}</p>
      </Show>
    </div>
  );
};

export default SelectCategory;
