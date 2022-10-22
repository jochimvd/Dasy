import { useNavigate, useParams } from "solid-app-router";
import { createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import SelectConsequence from "../../../components/SelectConsequence";
import SelectSeverity from "../../../components/SelectSeverity";
import { CategoryInput } from "../../../models/Category";
import { api, getUri } from "../../../utils/utils";
import { fetchCategory } from "./Category";

export const CategoryEdit = (props: { new?: boolean }) => {
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

  const [category, setCategory] = createStore({} as CategoryInput);

  const [saving, setSaving] = createSignal(false);

  createEffect(() => {
    setCategory({
      name: data()?.name ?? "",
      severity: { id: data()?.severity.id },
      consequence: { id: data()?.consequence.id },
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
      <section aria-labelledby="category-heading">
        <form>
          <div class="shadow sm:rounded-md sm:overflow-hidden">
            <div class="bg-white py-6 px-4 sm:p-6">
              <div>
                <h2
                  id="category-heading"
                  class="text-lg leading-6 font-medium text-gray-900"
                >
                  Category
                </h2>
                <p class="mt-1 text-sm text-gray-500">
                  Create a new category or edit an existing one.
                </p>
              </div>

              <div class="mt-6 grid grid-cols-4 gap-6">
                <div class="col-span-4 sm:col-span-2">
                  <label
                    for="name"
                    class="block text-sm font-medium text-gray-700"
                  >
                    Name
                  </label>
                  <input
                    type="text"
                    id="name"
                    class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                    placeholder="Category name"
                    value={category.name}
                    onInput={(e) => {
                      setCategory({ name: e.currentTarget.value });
                    }}
                  />
                </div>

                <div class="col-span-4 sm:col-start-1 sm:col-span-2">
                  <SelectSeverity
                    value={category.severity.id}
                    setSeverity={(id) => {
                      setCategory({ severity: { id: id } });
                    }}
                  />
                </div>

                <div class="col-span-4 sm:col-span-2">
                  <SelectConsequence
                    value={category.consequence.id}
                    setConsequence={(id) => {
                      setCategory({ consequence: { id: id } });
                    }}
                  />
                </div>
              </div>
            </div>

            <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
              <button
                type="submit"
                class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
                onClick={submitCategory}
              >
                {saving() ? "Saving..." : "Save"}
              </button>
            </div>
          </div>
        </form>
      </section>
    </>
  );
};

export default CategoryEdit;
