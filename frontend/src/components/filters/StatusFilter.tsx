import { createEffect, createSignal, For, Show } from "solid-js";
import { formatStatus, Status, Statuses } from "../../models/Status";
import { ChevronLeftS } from "../../utils/Icons";

type StatusFilterProps = {
  filter?: Status[];
  setFilter: (statuses: string) => void;
};

const StatusFilter = (props: StatusFilterProps) => {
  const [filter, setFilter] = createSignal<Status[]>(props.filter ?? []);
  const [show, setShow] = createSignal(false);

  createEffect(() => {
    props.setFilter(filter().join(","));
  });

  return (
    <>
      {/* Active: "bg-gray-100 text-gray-900", Not Active: "text-gray-700" */}
      <div class="flex justify-between items-center hover:bg-gray-100">
        <button
          class="group inline-flex flex-grow items-center font-medium text-gray-700 hover:text-gray-900 px-4 py-2"
          role="menuitem"
          tabindex="-1"
          id="menu-item-0"
          onClick={() => setShow(!show())}
        >
          <ChevronLeftS
            class="flex-shrink-0 -ml-1 mr-1 h-5 w-5 text-gray-400 group-hover:text-gray-500"
            classList={{ "rotate-180": !show(), "-rotate-90": show() }}
          />
          <span>Status</span>
          <Show when={filter().length > 0}>
            <span class="ml-1.5 rounded py-0.5 px-1.5 bg-gray-200 text-xs font-semibold text-gray-700 tabular-nums">
              {filter().length}
            </span>
          </Show>
        </button>
        <button
          classList={{
            // Use a class to hide instead of conditional rendering
            // because the element must remain in the DOM for the click event
            // to not count as a click outside of the menu.
            hidden: filter().length === 0,
          }}
          class="text-sm text-orange-600 mr-4"
          onClick={() => setFilter([])}
        >
          clear all
        </button>
      </div>
      <Show when={show()}>
        <fieldset class="space-y-3 mx-4 my-2">
          <For each={Statuses}>
            {(status) => (
              <div class="relative flex items-start">
                <div class="flex items-center h-5">
                  <input
                    id={status}
                    name={status}
                    type="checkbox"
                    class="hover:cursor-pointer focus:ring-orange-500 h-4 w-4 text-orange-600 border-gray-300 rounded"
                    checked={filter().includes(status)}
                    onChange={(e) => {
                      const newFilter = e.currentTarget.checked
                        ? [...filter(), status]
                        : filter().filter((c) => c !== status);

                      setFilter(newFilter);
                    }}
                  />
                </div>
                <div class="ml-3 text-sm">
                  <label for={status} class="text-gray-700">
                    <span class="truncate">{formatStatus(status)}</span>
                  </label>
                </div>
              </div>
            )}
          </For>
          <legend class="sr-only">Statuses</legend>
        </fieldset>
      </Show>
    </>
  );
};

export default StatusFilter;
