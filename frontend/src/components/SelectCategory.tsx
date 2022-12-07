import { For } from "solid-js";
import CategoryService from "../services/CategoryService";

type SelectProps = {
  value?: number;
  setCategory: (id: number) => void;
};

const SelectCategory = (props: SelectProps) => {
  const [data] = CategoryService().categoriesData();
  const categories = () => data()?._embedded?.categories;

  return (
    <>
      <label for="category" class="block text-sm font-medium text-gray-700">
        Category
      </label>
      <select
        id="category"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setCategory(Number(e.currentTarget.value));
        }}
        required
      >
        <option value="">Select a category</option>
        <For each={categories()?.sort((a, b) => a.name.localeCompare(b.name))}>
          {(category) => (
            <option value={category.id} selected={props.value === category.id}>
              {category.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectCategory;
