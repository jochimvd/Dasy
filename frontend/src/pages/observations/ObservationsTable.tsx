import { Link, useSearchParams } from "solid-app-router";
import { Component, For, JSXElement, Show } from "solid-js";
import { createStore } from "solid-js/store";
import { ObservationDto } from "../../models/Observation";
import { prettyFormatStatus } from "../../models/Status";
import { ObservationSearchParams } from "./Observations";

export type Column = {
  key: string;
  label: string;
  sortable: boolean;
  formatFn: (observation: ObservationDto) => JSX.Element;
};

const formatKey = (key: string) => {
  const index = key.indexOf("-") + 1;
  return [key.slice(0, index), key.slice(index)];
};

const keyColFormat = (observation: ObservationDto) => {
  return (
    <td class="px-2 md:px-6 py-4 text-sm font-medium text-gray-900">
      <span class="lg:whitespace-nowrap">
        <Link href={observation.id.toString()}>
          {formatKey(observation.key)[0]}
          <span class="whitespace-nowrap">{formatKey(observation.key)[1]}</span>
        </Link>
      </span>
    </td>
  );
};

const textColFormat = (child: string) => {
  return colFormat(<span class="line-clamp-2">{child}</span>);
};

const colFormat = (child: JSXElement) => {
  return <td class="px-2 md:px-6 py-4 text-sm text-gray-500">{child}</td>;
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
  formatFn: (o) => textColFormat(new Date(o.observedAt).toLocaleString()),
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
      )
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
                            fallback={
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                viewBox="0 0 20 20"
                                fill="currentColor"
                                class="w-4 h-4"
                              >
                                <path
                                  fill-rule="evenodd"
                                  d="M10 3a.75.75 0 01.55.24l3.25 3.5a.75.75 0 11-1.1 1.02L10 4.852 7.3 7.76a.75.75 0 01-1.1-1.02l3.25-3.5A.75.75 0 0110 3zm-3.76 9.2a.75.75 0 011.06.04l2.7 2.908 2.7-2.908a.75.75 0 111.1 1.02l-3.25 3.5a.75.75 0 01-1.1 0l-3.25-3.5a.75.75 0 01.04-1.06z"
                                  clip-rule="evenodd"
                                />
                              </svg>
                            }
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 20 20"
                              fill="currentColor"
                              class="w-4 h-4"
                              classList={{
                                "rotate-180": searchParams.orderDir === "ASC",
                              }}
                            >
                              <path
                                fill-rule="evenodd"
                                d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z"
                                clip-rule="evenodd"
                              />
                            </svg>
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
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke-width="1.5"
                      stroke="currentColor"
                      class="mx-auto h-12 w-12 text-gray-400"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M21 7.5l-2.25-1.313M21 7.5v2.25m0-2.25l-2.25 1.313M3 7.5l2.25-1.313M3 7.5l2.25 1.313M3 7.5v2.25m9 3l2.25-1.313M12 12.75l-2.25-1.313M12 12.75V15m0 6.75l2.25-1.313M12 21.75V19.5m0 2.25l-2.25-1.313m0-16.875L12 2.25l2.25 1.313M21 14.25v2.25l-2.25 1.313m-13.5 0L3 16.5v-2.25"
                      />
                    </svg>

                    {props.observations.length === 0
                      ? () => {
                          return (
                            <>
                              <h3 class="mt-2 text-sm font-medium text-gray-900">
                                No observations found
                              </h3>
                              <p class="mt-1 text-sm text-gray-500">
                                Try adjusting your search or filter to find what
                                you're looking for.
                              </p>
                            </>
                          );
                        }
                      : () => {
                          return (
                            <h3 class="mt-2 text-sm font-medium text-gray-900">
                              Loading...
                            </h3>
                          );
                        }}
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
                  <td class="px-2 md:px-6 py-4 text-right text-sm font-medium">
                    <Link
                      href={observation.id + "/edit"}
                      class="text-orange-600 hover:text-orange-900"
                    >
                      Edit
                    </Link>
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
