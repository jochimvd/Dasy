import { createSignal, For, Show } from "solid-js";
import {
  observationTableColumns,
  setObservationTableColumns,
} from "../../pages/observations/ObservationsTable";
import { clickOutside } from "../../utils/Directives";
import { AdjustmentsHorizontalOutline } from "../../utils/Icons";

const ColumnSelector = () => {
  let columnButton;
  false && clickOutside; // See: https://github.com/solidjs/solid/issues/1005

  const [flyout, setFlyout] = createSignal(false);

  return (
    <>
      <div class="relative inline-block text-left">
        <div>
          <button
            onClick={() => setFlyout(!flyout())}
            type="button"
            class="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-orange-500"
            id="menu-button"
            aria-expanded="true"
            aria-haspopup="true"
          >
            <AdjustmentsHorizontalOutline class="w-5 h-5" />
          </button>
        </div>

        <Show when={flyout()}>
          <div
            class="z-40 origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
            role="menu"
            aria-orientation="vertical"
            aria-labelledby="menu-button"
            tabindex="-1"
            use:clickOutside={[() => setFlyout(false), columnButton]}
          >
            <div class="py-1" role="none">
              <div class="flex justify-between items-center">
                <div class="group inline-flex flex-grow items-center font-medium text-gray-700 hover:text-gray-900 px-4 py-2">
                  <span>Columns</span>
                </div>
                <button
                  classList={{
                    // Use a class to hide instead of conditional rendering
                    // because the element must remain in the DOM for the click event
                    // to not count as a click outside of the menu.
                    hidden: observationTableColumns.column.every((e) => e.show),
                  }}
                  class="text-sm text-orange-600 mr-4"
                  onClick={() =>
                    setObservationTableColumns(
                      "column",
                      () => true,
                      "show",
                      () => true
                    )
                  }
                >
                  select all
                </button>
                <button
                  classList={{
                    // Use a class to hide instead of conditional rendering
                    // because the element must remain in the DOM for the click event
                    // to not count as a click outside of the menu.
                    hidden: !observationTableColumns.column.every(
                      (e) => e.show
                    ),
                  }}
                  class="text-sm text-orange-600 mr-4"
                  onClick={() =>
                    setObservationTableColumns("column", {}, (col) => ({
                      show: col.default,
                    }))
                  }
                >
                  reset
                </button>
              </div>
              <fieldset class="space-y-3 mx-4 my-2">
                <For each={observationTableColumns.column}>
                  {(columnEntry) => (
                    <div class="relative flex items-start">
                      <div class="flex items-center h-5">
                        <input
                          id={columnEntry.col.label}
                          name={columnEntry.col.label}
                          type="checkbox"
                          class="hover:cursor-pointer focus:ring-orange-500 h-4 w-4 text-orange-600 border-gray-300 rounded disabled:cursor-not-allowed disabled:opacity-50"
                          checked={columnEntry.show}
                          disabled={columnEntry.col.key === "key"}
                          onChange={(e) => {
                            setObservationTableColumns(
                              "column",
                              (e) => e.col.key === columnEntry.col.key,
                              "show",
                              () => e.currentTarget.checked
                            );
                          }}
                        />
                      </div>
                      <div class="ml-3 text-sm">
                        <label
                          for={columnEntry.col.label}
                          class="text-gray-700"
                          classList={{
                            "opacity-50": columnEntry.col.key === "key",
                          }}
                        >
                          <span class="truncate">{columnEntry.col.label}</span>
                        </label>
                      </div>
                    </div>
                  )}
                </For>
                <legend class="sr-only">Columns</legend>
              </fieldset>
            </div>
          </div>
        </Show>
      </div>
    </>
  );
};

export default ColumnSelector;
