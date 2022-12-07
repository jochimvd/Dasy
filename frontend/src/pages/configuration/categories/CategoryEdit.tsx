import { useNavigate, useRouteData } from "@solidjs/router";
import { createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import SelectConsequence from "../../../components/SelectConsequence";
import SelectSeverity from "../../../components/SelectSeverity";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { CategoryInput } from "../../../models/Category";
import CategoryService, {
  CategoryData,
} from "../../../services/CategoryService";

export const CategoryEdit = () => {
  const navigate = useNavigate();

  const categoryService = CategoryService();
  const [data, { mutate }] = useRouteData<CategoryData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<CategoryInput>({});
  const updateState = (field: keyof CategoryInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const category = await (data()?.id
        ? categoryService.updateCategory
        : categoryService.createCategory)(state);

      mutate(category);
      navigate("/configuration/categories/" + category.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const category = data();
    if (category && category.id) {
      const { _links, ...rest } = category;
      setState(rest);
    }
  });

  return (
    <ConfigurationEditLayout
      heading={"Category"}
      subheading={"Create a new category or edit an existing one."}
      saving={saving}
      onSubmit={submitForm}
    >
      <div class="mt-6 grid grid-cols-4 gap-6">
        <div class="col-span-4 sm:col-span-2">
          <label for="name" class="block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            id="name"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            placeholder="Category name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>

        <div class="col-span-4 sm:col-start-1 sm:col-span-2">
          <SelectSeverity
            value={state.severity?.id}
            setSeverity={(id) => {
              setState("severity", "id", id);
            }}
          />
        </div>

        <div class="col-span-4 sm:col-span-2">
          <SelectConsequence
            value={state.consequence?.id}
            setConsequence={(id) => {
              setState("consequence", "id", id);
            }}
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default CategoryEdit;
