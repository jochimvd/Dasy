import { Link } from "solid-app-router";
import { Component, createResource, For, Show } from "solid-js";
import { api } from "../../utils";
import { Observation } from "./Observation";

type Response = {
  _embedded: {
    observations: Observation[];
  };
};

const fetchObservations = async () => {
  const res = await api.get("observations").json<Response>();
  return res._embedded.observations;
};

const Observations: Component = () => {
  const [observations] = createResource(fetchObservations);

  return (
    <>
      <div class="flex mb-5">
        <h1 class="text-xl">Observations</h1>

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
                Key
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Observer
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Observed at
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Danger
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Description
              </th>
              <th class="px-4 py-2 font-medium text-left text-gray-900 whitespace-nowrap">
                Actions
              </th>
            </tr>
          </thead>

          <tbody class="divide-y divide-gray-200">
            <For each={observations()} fallback={<div>Loading...</div>}>
              {(observation) => (
                <tr>
                  <td class="px-4 py-2 font-medium text-gray-900 whitespace-nowrap">
                    <Link href={`${observation.id}`}>{observation.key}</Link>
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    {observation.observer}
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    {new Date(observation.observedAt).toLocaleString()}
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    {observation.immediateDanger ? "Yes" : "No"}
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    {observation.description}
                  </td>
                  <td class="px-4 py-2 text-gray-700 whitespace-nowrap">
                    <Link
                      class="px-3 py-2 text-xs font-medium text-center text-blue-700 hover:text-blue-800"
                      href={observation.id + "/edit"}
                    >
                      Edit
                    </Link>
                    <Show when={observation._links?.delete}>
                      <Link
                        class="px-3 py-2 text-xs font-medium text-center text-white bg-red-700 rounded-lg hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-800"
                        href={observation.id + "/delete"}
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

export default Observations;
