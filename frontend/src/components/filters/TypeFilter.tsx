import { createEffect, createSignal, For, Show, Suspense } from "solid-js";
import TypeService from "../../services/TypeService";
import { ChevronLeftS } from "../../utils/Icons";

type TypeFilterProps = {
  filter?: number[];
  setFilter: (types: string) => void;
};

const TypeFilter = (props: TypeFilterProps) => {
  const [data] = TypeService().typesData();
  const types = () =>
    data()?._embedded?.types.sort((a, b) => a.name.localeCompare(b.name));

  const [filter, setFilter] = createSignal<number[]>(props.filter ?? []);
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
          <span>Type</span>
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
          <Suspense
            fallback={<div class="text-gray-500 text-sm">Loading...</div>}
          >
            <For each={types()}>
              {(type) => (
                <div class="relative flex items-start">
                  <div class="flex items-center h-5">
                    <input
                      id={type.name}
                      name={type.name}
                      type="checkbox"
                      class="hover:cursor-pointer focus:ring-orange-500 h-4 w-4 text-orange-600 border-gray-300 rounded"
                      checked={filter().includes(type.id)}
                      onChange={(e) => {
                        const newFilter = e.currentTarget.checked
                          ? [...filter(), type.id]
                          : filter().filter((c) => c !== type.id);

                        setFilter(newFilter);
                      }}
                    />
                  </div>
                  <div class="ml-3 text-sm">
                    <label for={type.name} class="text-gray-700">
                      <span class="line-clamp-1" title={type.name}>
                        {type.name}
                      </span>
                    </label>
                  </div>
                </div>
              )}
            </For>
          </Suspense>
          <legend class="sr-only">Types</legend>
        </fieldset>
      </Show>
    </>
  );
};

export default TypeFilter;
