import { useSearchParams } from "@solidjs/router";
import { createEffect, createSignal, For, on, Show } from "solid-js";
import { ObservationSearchParams } from "../../pages/observations/Observations";
import { clickOutside } from "../../utils/Directives";
import { ChevronUpDownS, MagnifyingGlassS, XMarkS } from "../../utils/Icons";

const SearchFilter = () => {
  let columnButton;
  false && clickOutside; // See: https://github.com/solidjs/solid/issues/1005

  let timer: number;
  const waitTime = 400; // Wait time in milliseconds

  const [searchParams, setSearchParams] =
    useSearchParams<ObservationSearchParams>();

  const [column, setColumn] = createSignal({ label: "All", key: "search" });
  const [value, setValue] = createSignal(searchParams.search || "");
  const [flyout, setFlyout] = createSignal(false);

  const searchFilters: { label: string; key: string }[] = [
    { label: "All", key: "search" },
    { label: "Site", key: "siteName" },
    { label: "Observer", key: "observerName" },
    { label: "Category", key: "categoryName" },
    { label: "Observed Company", key: "observedCompanyName" },
  ];

  const emptyParams = searchFilters.reduce((acc, { key }) => {
    acc[key] = "";
    return acc;
  }, {} as { [key: string]: string });

  const clearFilters = () => {
    setSearchParams(emptyParams);
  };

  createEffect(
    on(column, (f) => {
      if (value()) {
        const newParams = { ...emptyParams, [f.key]: value().trim() };
        setSearchParams(newParams);
      }
    })
  );

  return (
    <>
      <div class="relative text-left">
        <div class="flex rounded-md shadow-sm">
          <div class="relative flex items-stretch flex-grow focus-within:z-10">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <MagnifyingGlassS />
            </div>
            <input
              type="text"
              name="search"
              class="focus:ring-orange-500 focus:border-orange-500 block w-full rounded-none rounded-l-md pl-10 sm:text-sm border-gray-300"
              placeholder="Search"
              value={value()}
              onKeyUp={(e) => {
                setValue(e.currentTarget.value);

                // Clear timer
                clearTimeout(timer);

                // Wait for waitTime ms and then send the result
                timer = setTimeout(() => {
                  setSearchParams({ [column().key]: value().trim(), page: "" });
                }, waitTime);
              }}
            />

            <Show when={value()}>
              <div class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-500 hover:text-gray-700">
                <button
                  onClick={() => {
                    clearFilters();
                    setValue("");
                  }}
                >
                  <XMarkS />
                </button>
              </div>
            </Show>
          </div>
          <button
            type="button"
            class="-ml-px relative inline-flex items-center space-x-1 pl-2 pr-3 py-2 border border-gray-300 text-sm font-medium rounded-r-md text-gray-700 bg-gray-50 hover:bg-gray-100 focus:outline-none focus:ring-1 focus:ring-orange-500 focus:border-orange-500"
            ref={columnButton}
            onClick={() => setFlyout(!flyout())}
          >
            <ChevronUpDownS />
            <div class="truncate sm:max-w-[5rem]">{column().label}</div>
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
              <div class="group inline-flex flex-grow items-center font-medium text-gray-700 hover:text-gray-900 px-4 py-2">
                <span>Search column</span>
              </div>
              <fieldset class="space-y-3 mx-4 my-2">
                <For each={searchFilters}>
                  {(searchFilter) => (
                    <div class="flex items-center">
                      <input
                        id={searchFilter.key}
                        name="notification-method"
                        type="radio"
                        class="hover:cursor-pointer focus:ring-orange-500 h-4 w-4 text-orange-600 border-gray-300"
                        checked={column().key === searchFilter.key}
                        onClick={() => {
                          setColumn(searchFilter);
                          setFlyout(false);
                        }}
                      />
                      <label
                        for={searchFilter.key}
                        class="ml-3 block text-sm font-medium text-gray-700"
                      >
                        {searchFilter.label}
                      </label>
                    </div>
                  )}
                </For>
                <legend class="sr-only">Search Filter</legend>
              </fieldset>
            </div>
          </div>
        </Show>
      </div>
    </>
  );
};

export default SearchFilter;
