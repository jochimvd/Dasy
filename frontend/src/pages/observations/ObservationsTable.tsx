import { A, useSearchParams } from "@solidjs/router";
import { Component, For, JSXElement, onMount, Show } from "solid-js";
import { createStore } from "solid-js/store";
import { ObservationDto } from "../../models/Observation";
import { prettyFormatStatus } from "../../models/Status";
import { ChevronDownS, ChevronUpDownS } from "../../utils/Icons";
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
      <span class="lg:whitespace-nowrap hover:underline">
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
  key: "observedCompanyName",
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

export const siteCol: Column = {
  key: "siteName",
  label: "Site",
  sortable: true,
  formatFn: (o) => textColFormat(o.site),
};

export const typeCol: Column = {
  key: "typeName",
  label: "Type",
  sortable: true,
  formatFn: (o) => textColFormat(o.type.name),
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
      { default: false, show: false, col: typeCol },
      { default: false, show: false, col: categoryCol },
      { default: false, show: false, col: siteCol },
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

  let table: HTMLTableElement;

  const isOverflowing = ({
    clientWidth,
    clientHeight,
    scrollWidth,
    scrollHeight,
  }: Element) => {
    return scrollHeight > clientHeight || scrollWidth > clientWidth;
  };

  onMount(() => {
    let isDown = false;
    let startX: number;
    let scrollLeft: number;

    table.addEventListener("mouseenter", (e) => {
      if (!isOverflowing(table)) return;
      table.classList.add("cursor-grab");
    });
    table.addEventListener("mousedown", (e) => {
      if (!isOverflowing(table)) return;
      isDown = true;
      table.classList.add("cursor-grabbing");
      startX = e.pageX - table.offsetLeft;
      scrollLeft = table.scrollLeft;
    });
    table.addEventListener("mouseleave", () => {
      isDown = false;
      table.classList.remove("cursor-grabbing", "cursor-grab");
    });
    table.addEventListener("mouseup", () => {
      isDown = false;
      table.classList.remove("cursor-grabbing");
    });
    table.addEventListener("mousemove", (e) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - table.offsetLeft;
      const walk = x - startX;
      table.scrollLeft = scrollLeft - walk;
    });
  });

  return (
    <div class="overflow-x-auto" ref={table!}>
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
                        <div class="px-2 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          {column.label}
                        </div>
                      }
                    >
                      <button
                        class="flex items-center gap-1 px-2 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
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
          <For each={props.observations}>
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
    </div>
  );
};

export default ObservationsTable;
