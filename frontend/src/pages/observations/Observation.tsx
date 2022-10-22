import { Link, useParams } from "solid-app-router";
import { Component, createResource, Show, Suspense } from "solid-js";
import { ObservationDto } from "../../models/Observation";
import { prettyFormatStatus, Status } from "../../models/Status";
import { api } from "../../utils/utils";

export const fetchObservation = async (id: string) => {
  return await api.get(`observations/${id}`).json<ObservationDto>();
};

const Observation: Component = () => {
  const params = useParams();
  const [observation] = createResource(() => params.id, fetchObservation);

  return (
    <>
      <div class="">
        <Suspense fallback={<div>Loading...</div>}>
          <div class="bg-white shadow overflow-hidden sm:rounded-lg">
            <div class="px-4 py-5 border-b border-gray-200 sm:px-6">
              <div class="-ml-4 flex items-center justify-start flex-wrap sm:flex-nowrap">
                <div class="ml-4">
                  <h3 class="text-lg leading-6 font-medium text-gray-900">
                    {observation()?.key}
                  </h3>
                </div>
                <div class="ml-4 mr-auto">
                  {prettyFormatStatus(observation()?.status, "large")}
                </div>
                <div class="ml-4">
                  <Show when={observation()?._links?.put}>
                    <Link
                      href="edit"
                      class="inline-flex items-center px-2.5 py-1.5 border border-orange-600 shadow-sm text-xs font-medium rounded text-orange-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                    >
                      Edit
                    </Link>
                  </Show>
                </div>
              </div>
            </div>
            <div class="px-4 py-5 border-b border-gray-200 sm:px-6">
              <dl class="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">Observer</dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.observer.firstName}{" "}
                    {observation()?.observer.lastName}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">
                    Observer Company
                  </dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.observerCompany}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">Observed At</dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {new Date(observation()?.observedAt ?? "").toLocaleString()}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">
                    Observed Company
                  </dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.observedCompany}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">Category</dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.category.name}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">Location</dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.location.name}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">
                    Immediate Danger
                  </dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.immediateDanger ? "Yes" : "No"}
                  </dd>
                </div>
                <div class="sm:col-span-1">
                  <dt class="text-sm font-medium text-gray-500">Type</dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.type}
                  </dd>
                </div>
              </dl>
            </div>
            <div class="px-4 py-5 sm:px-6">
              <dl class="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
                <div class="sm:col-span-2">
                  <dt class="text-sm font-medium text-gray-500">Description</dt>
                  <dd class="mt-1 text-sm text-gray-900">
                    {observation()?.description}
                  </dd>
                </div>
                <Show when={observation()?.actionsTaken}>
                  <div class="sm:col-span-2">
                    <dt class="text-sm font-medium text-gray-500">
                      Actions Taken
                    </dt>
                    <dd class="mt-1 text-sm text-gray-900">
                      {observation()?.actionsTaken}
                    </dd>
                  </div>
                </Show>
                <Show when={observation()?.furtherActions}>
                  <div class="sm:col-span-2">
                    <dt class="text-sm font-medium text-gray-500">
                      Further Actions
                    </dt>
                    <dd class="mt-1 text-sm text-gray-900">
                      {observation()?.furtherActions}
                    </dd>
                  </div>
                </Show>
              </dl>
            </div>
          </div>
        </Suspense>
      </div>
    </>
  );
};

export default Observation;
