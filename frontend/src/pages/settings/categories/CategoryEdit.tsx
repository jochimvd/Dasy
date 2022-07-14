import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import SelectConsequence from "../../../components/SelectConsequence";
import SelectSeverity from "../../../components/SelectSeverity";
import { api, getUri } from "../../../utils";
import { fetchCategory } from "./Category";

export type CategoryInput = {
  name: string;
  severity: string;
  consequence: string;
};

export const CategoryEdit = (props: { new?: boolean }) => {
  const emptyCategory: CategoryInput = {
    name: "",
    severity: "",
    consequence: "",
  };

  const putCategory = async () => {
    return props.new
      ? await api.post("categories", { json: category })
      : await api.put(new URL(data()?._links?.self.href ?? ""), {
          json: category,
        });
  };

  const params = useParams();
  const navigate = useNavigate();

  const [data] = createResource(() => params.id, fetchCategory);

  const [category, setCategory] = createStore(emptyCategory);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setCategory({
      name: data()?.name ?? emptyCategory.name,
      severity: data()?.severity._links?.self.href ?? emptyCategory.severity,
      consequence:
        data()?.consequence._links?.self.href ?? emptyCategory.consequence,
    });
  });

  const submitCategory = (e: Event) => {
    e.preventDefault();
    setSaving(true);

    putCategory().then((res) => {
      if (res.ok) {
        navigate("/settings" + getUri(res.headers.get("Location") ?? ""), {
          replace: true,
        });
      }
    });
  };

  return (
    <>
      <h1 class="text-xl mb-5">Category</h1>
      <form>
        <div class="mb-6">
          <label
            for="name"
            class="block mb-2 text-sm font-medium text-gray-900"
          >
            Name
          </label>
          <input
            id="name"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            placeholder="Category name"
            value={category.name}
            onInput={(e) => {
              setCategory({ name: e.currentTarget.value });
            }}
          />
        </div>
        <div class="mb-6">
          <SelectSeverity
            value={category.severity}
            setSeverity={(url) => {
              setCategory({ severity: url });
            }}
          />
        </div>
        <div class="mb-6">
          <SelectConsequence
            value={category.consequence}
            setConsequence={(url) => {
              setCategory({ consequence: url });
            }}
          />
        </div>
        <button
          type="submit"
          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          onClick={submitCategory}
        >
          {saving() ? "Saving..." : "Save"}
        </button>
      </form>
    </>
  );
};

export default CategoryEdit;
