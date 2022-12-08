import { A, useSearchParams } from "@solidjs/router";
import { Component, For, JSXElement, Show } from "solid-js";
import { createStore } from "solid-js/store";
import { ObservationDto } from "../../models/Observation";
import { prettyFormatStatus } from "../../models/Status";
import { ChevronDownS, ChevronUpDownS, CubeOutline } from "../../utils/Icons";
import { formatDate } from "../../utils/utils";
import { ObservationSearchParams } from "./Observations";

export type Column = {
  key: string;
  label: string;
  sortable: boolean;
  formatFn: (observation: ObservationDto) => JSXElement;
};

const formatKey = (key: string) => {
  const index = key.indexOf("-") + 1;
  return [key.slice(0, index), key.slice(index)];
};

const keyColFormat = (observation: ObservationDto) => {
  return (
    <td class="px-2 md:px-6 py-4 text-sm font-medium text-gray-900">
      <span class="lg:whitespace-nowrap">
        <A href={observation.id.toString()}>
          {formatKey(observation.key)[0]}
          <span class="whitespace-nowrap">{formatKey(observation.key)[1]}</span>
        </A>
      </span>
    </td>
  );
};

const textColFormat = (child: string, title?: string) => {
  return colFormat(
    <span class="line-clamp-2" title={title}>
      {child}
    </span>
  );
};

const colFormat = (child: JSXElement, extraClass?: string) => {
  return (
    <td class={`px-2 md:px-6 py-4 text-sm text-gray-500 ${extraClass}`}>
      {child}
    </td>
  );
};

export const keyCol: Column = {
  key: "key",
  label: "Key",
  sortable: true,
  formatFn: keyColFormat,
};

export const observerCol: Column = {
  key: "observerName",
  label: "Observer",
  sortable: true,
  formatFn: (o) =>
    textColFormat(o.observer.firstName + " " + o.observer.lastName),
};

export const observedAtCol: Column = {
  key: "observedAt",
  label: "Observed At",
  sortable: true,
  formatFn: (o) =>
    textColFormat(
      new Date(o.observedAt).toLocaleDateString(),
      formatDate(new Date(o.observedAt))
    ),
};

export const observedCompanyCol: Column = {
  key: "observedCompany",
  label: "Observed Company",
  sortable: true,
  formatFn: (o) => textColFormat(o.observedCompany),
};

export const descriptionCol: Column = {
  key: "description",
  label: "Description",
  sortable: false,
  formatFn: (o) => textColFormat(o.description),
};

export const statusCol: Column = {
  key: "status",
  label: "Status",
  sortable: true,
  formatFn: (o) => colFormat(prettyFormatStatus(o.status)),
};

export const categoryCol: Column = {
  key: "categoryName",
  label: "Category",
  sortable: true,
  formatFn: (o) => textColFormat(o.category.name),
};

export const locationCol: Column = {
  key: "locationName",
  label: "Location",
  sortable: true,
  formatFn: (o) => textColFormat(o.location.name),
};

export const immediateDangerCol: Column = {
  key: "immediateDanger",
  label: "Danger",
  sortable: true,
  formatFn: (o) =>
    colFormat(
      o.immediateDanger ? (
        <span class="whitespace-nowrap inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-red-100 text-red-800">
          Yes
        </span>
      ) : (
        ""
      ),
      "text-center"
    ),
};

export const [observationTableColumns, setObservationTableColumns] =
  createStore({
    column: [
      { default: true, show: true, col: keyCol },
      { default: false, show: false, col: immediateDangerCol },
      { default: true, show: true, col: observerCol },
      { default: true, show: true, col: observedAtCol },
      { default: false, show: false, col: observedCompanyCol },
      { default: false, show: false, col: statusCol },
      { default: false, show: false, col: categoryCol },
      { default: false, show: false, col: locationCol },
      { default: true, show: true, col: descriptionCol },
    ],
  });

type ObservationTableProps = {
  loading: boolean;
  observations: ObservationDto[];
};

const ObservationsTable: Component<ObservationTableProps> = (props) => {
  const [searchParams, setSearchParams] =
    useSearchParams<ObservationSearchParams>();

  const setSort = (col?: string) => {
    const sortOptions = ["ASC", "DESC", undefined];
    let sort = sortOptions[0];

    if (col === searchParams.orderBy) {
      const index = sortOptions.indexOf(searchParams.orderDir) + 1;
      sort = sortOptions[index % sortOptions.length];

      if (sort === undefined) {
        col = undefined;
      }
    }

    setSearchParams({
      orderBy: col,
      orderDir: sort,
    });
  };

  return (
    <>
      <table class="w-full divide-y divide-gray-200">
        <thead class="bg-gray-50 whitespace-nowrap">
          <tr>
            <For each={observationTableColumns.column.filter((c) => c.show)}>
              {(columnEntry) => {
                const column = columnEntry.col;
                return (
                  <th scope="col">
                    <Show
                      when={column.sortable}
                      fallback={
                        <div class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {column.label}
                        </div>
                      }
                    >
                      <button
                        class="flex items-center gap-1 px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        onClick={() => {
                          setSort(column.key);
                        }}
                      >
                        <span>{column.label}</span>
                        <span class="inline-flex text-gray-400">
                          <Show
                            when={searchParams.orderBy === column.key}
                            fallback={<ChevronUpDownS class="w-4 h-4" />}
                          >
                            <ChevronDownS
                              class="w-4 h-4"
                              classList={{
                                "rotate-180": searchParams.orderDir === "ASC",
                              }}
                            />
                          </Show>
                        </span>
                      </button>
                    </Show>
                  </th>
                );
              }}
            </For>
            <Show when={props.observations.some((o) => o._links?.put)}>
              <th scope="col" class="relative px-6 py-3">
                <span class="sr-only">Edit</span>
              </th>
            </Show>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <For
            each={props.observations}
            fallback={
              <td colspan={100}>
                <div class="w-full my-12">
                  <div class="text-center">
                    <CubeOutline class="mx-auto h-12 w-12 text-gray-400" />
                    {props.loading ? (
                      <h3 class="mt-2 text-sm font-medium text-gray-900">
                        Loading...
                      </h3>
                    ) : (
                      <>
                        <h3 class="mt-2 text-sm font-medium text-gray-900">
                          No observations found
                        </h3>
                        <p class="mt-1 text-sm text-gray-500">
                          Try adjusting your search or filter to find what
                          you're looking for.
                        </p>
                      </>
                    )}
                  </div>
                </div>
              </td>
            }
          >
            {(observation) => (
              <tr>
                <For
                  each={observationTableColumns.column.filter((c) => c.show)}
                >
                  {(columnEntry) => columnEntry.col.formatFn(observation)}
                </For>
                <Show when={observation._links?.put}>
                  <td class="px-2 md:px-6 py-4 text-sm font-medium">
                    <A
                      href={observation.id + "/edit"}
                      class="text-orange-600 hover:text-orange-900"
                    >
                      Edit
                    </A>
                  </td>
                </Show>
              </tr>
            )}
          </For>
        </tbody>
      </table>
    </>
  );
};

export default ObservationsTable;
