import { Link } from "solid-app-router";
import { Component, createResource, For, Show } from "solid-js";
import { SeverityDto } from "../../../models/Severity";
import { api } from "../../../utils/utils";

type Response = {
  _embedded: {
    severities: SeverityDto[];
  };
};

export const fetchSeverities = async () => {
  const res = await api.get("severities").json<Response>();
  return res._embedded.severities;
};

const Severities: Component = () => {
  const [severities] = createResource(fetchSeverities);

  return (
    <>
      <section aria-labelledby="severities-heading">
        <div class="bg-white pt-6 shadow sm:rounded-md sm:overflow-hidden">
          <div class="flex justify-between px-4 sm:px-6">
            <h2
              id="severities-heading"
              class="text-lg leading-6 font-medium text-gray-900"
            >
              Severities
            </h2>

            <Link
              href="create"
              class="inline-flex items-center px-2.5 py-1.5 border border-orange-600 shadow-sm text-xs font-medium rounded text-orange-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
            >
              New Severity
            </Link>
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
                          Level
                        </th>
                        {/* <!--
                    `relative` is added here due to a weird bug in Safari that causes `sr-only` headings to introduce overflow on the body on mobile.
                  --> */}
                        <th
                          scope="col"
                          class="relative px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                          <span class="sr-only">Edit</span>
                        </th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <For each={severities()} fallback={<div>Loading...</div>}>
                        {(severity) => (
                          <tr>
                            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                              <Link href={`${severity.id}`}>
                                {severity.name}
                              </Link>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {severity.level}
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                              <Link
                                href={severity.id + "/edit"}
                                class="text-orange-600 hover:text-orange-900"
                              >
                                Edit
                              </Link>
                            </td>
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

export default Severities;
