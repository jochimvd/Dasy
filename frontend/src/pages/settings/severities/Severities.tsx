import { Link } from "solid-app-router";
import { Component, createResource, For, Show } from "solid-js";
import { api } from "../../../utils";
import { Severity } from "./Severity";

type Response = {
  _embedded: {
    severities: Severity[];
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
      <div class="flex mb-5">
        <h1 class="text-xl">Severities</h1>

        <Link
          class="ml-4 px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          href="create"
        >
          New
        </Link>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full text-sm divide-y-2 divide-gray-200">
          <thead>
            <tr>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Name
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Level
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Actions
              </th>
            </tr>
          </thead>

          <tbody class="divide-y divide-gray-200">
            <For each={severities()} fallback={<div>Loading...</div>}>
              {(severity) => (
                <tr>
                  <td class="px-4 py-2 font-medium text-gray-900 whitespace-nowrap">
                    <Link href={`${severity.id}`}>{severity.name}</Link>
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    {severity.level}
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    <Link
                      class="px-3 py-2 text-xs font-medium text-center text-blue-700 hover:text-blue-800"
                      href={severity.id + "/edit"}
                    >
                      Edit
                    </Link>
                    <Show when={severity._links?.delete}>
                      <Link
                        class="px-3 py-2 text-xs font-medium text-center text-white bg-red-700 rounded-lg hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-800"
                        href={severity.id + "/delete"}
                      >
                        Delete
                      </Link>
                    </Show>
                  </td>
                </tr>
              )}
            </For>
          </tbody>
        </table>
      </div>
    </>
  );
};

export default Severities;
