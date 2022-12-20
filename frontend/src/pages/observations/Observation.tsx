import { A, useNavigate, useRouteData } from "@solidjs/router";
import { Component, Show } from "solid-js";
import { prettyFormatStatus } from "../../models/Status";
import ObservationService, {
  ObservationData,
} from "../../services/ObservationService";
import { formatDate } from "../../utils/utils";

const Observation: Component = () => {
  const navigate = useNavigate();
  const observationService = ObservationService();
  const [observation] = useRouteData<ObservationData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this observation?")) {
      observationService.deleteObservation(observation()!.id.toString());
      navigate("/observations");
    }
  };

  return (
    <>
      <div class="">
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
                  <A
                    href="edit"
                    class="inline-flex items-center px-2.5 py-1.5 border border-orange-600 shadow-sm text-xs font-medium rounded text-orange-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                  >
                    Edit
                  </A>
                </Show>
                <Show when={observation()?._links?.delete}>
                  <button
                    class="ml-2 inline-flex items-center px-2.5 py-1.5 border border-red-600 shadow-sm text-xs font-medium rounded text-red-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                    onClick={() => handleDelete()}
                  >
                    Delete
                  </button>
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
                  {observation()?.observer.company}
                </dd>
              </div>
              <div class="sm:col-span-1">
                <dt class="text-sm font-medium text-gray-500">Observed At</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {observation()?.observedAt &&
                    formatDate(new Date(observation()!.observedAt))}
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
                <dt class="text-sm font-medium text-gray-500">Site</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {observation()?.site}
                </dd>
              </div>
              <div class="sm:col-span-1">
                <dt class="text-sm font-medium text-gray-500">Type</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {observation()?.type.name}
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
            </dl>
          </div>
          <div class="px-4 py-5 sm:px-6">
            <dl class="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
              <div class="sm:col-span-2">
                <dt class="text-sm font-medium text-gray-500">Description</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {observation()?.description ?? "No description provided."}
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
      </div>
    </>
  );
};

export default Observation;
