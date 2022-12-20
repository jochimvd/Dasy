import { Link, useRouteData } from "@solidjs/router";
import { Component, For } from "solid-js";
import { CategoriesData } from "../../../services/CategoryService";

const Categories: Component = () => {
  const [data] = useRouteData<CategoriesData>();
  const categories = () =>
    data()?._embedded?.categories.sort((a, b) => a.name.localeCompare(b.name));

  return (
    <>
      <section aria-labelledby="categories-heading">
        <div class="bg-white pt-6 shadow sm:rounded-md sm:overflow-hidden">
          <div class="flex items-center justify-between px-4 sm:px-6">
            <h2
              id="categories-heading"
              class="text-lg leading-6 font-medium text-gray-900"
            >
              Categories
            </h2>

            {data()?._links.post && (
              <Link
                href="create"
                class="inline-flex items-center px-2.5 py-1.5 border border-orange-600 shadow-sm text-xs font-medium rounded text-orange-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
              >
                New Category
              </Link>
            )}
          </div>
          <div class="mt-6 flex flex-col">
            <div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
              <div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
                <div class="overflow-hidden border-t border-gray-200">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                      <tr>
                        <th
                          scope="col"
                          class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                          Name
                        </th>
                        <th
                          scope="col"
                          class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                          Reoccurrence
                        </th>
                        <th
                          scope="col"
                          class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                          Severity
                        </th>
                        {/*
                          `relative` is added here due to a weird bug in Safari that causes `sr-only` headings to introduce overflow on the body on mobile.
                        */}
                        {categories()?.some((c) => c._links?.put) && (
                          <th
                            scope="col"
                            class="relative px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                          >
                            <span class="sr-only">Edit</span>
                          </th>
                        )}
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <For each={categories()} fallback={<div>Loading...</div>}>
                        {(category) => (
                          <tr>
                            <td class="px-6 py-4 text-sm font-medium text-gray-900">
                              <span class="line-clamp-2">
                                <Link href={`${category.id}`}>
                                  {category.name}
                                </Link>
                              </span>
                            </td>
                            <td class="px-6 py-4 text-sm text-gray-500">
                              <span
                                class="cursor-help"
                                title={category.reoccurrence.name}
                              >
                                {category.reoccurrence.rate}
                              </span>
                            </td>
                            <td class="px-6 py-4 text-sm text-gray-500">
                              <span>{category.severityLevel}</span>
                            </td>
                            {category._links?.put && (
                              <td class="px-6 py-4 text-right text-sm font-medium">
                                <Link
                                  href={category.id + "/edit"}
                                  class="text-orange-600 hover:text-orange-900"
                                >
                                  Edit
                                </Link>
                              </td>
                            )}
                          </tr>
                        )}
                      </For>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default Categories;
