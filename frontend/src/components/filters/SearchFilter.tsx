import { useSearchParams } from "solid-app-router";
import {
  batch,
  createEffect,
  createSignal,
  For,
  on,
  Show,
  untrack,
} from "solid-js";
import { ObservationSearchParams } from "../../pages/observations/Observations";
import { clickOutside } from "../../utils/Directives";

const SearchFilter = () => {
  let columnButton;
  false && clickOutside; // See: https://github.com/solidjs/solid/issues/1005

  let timer = 0;
  const waitTime = 500; // Wait time in milliseconds

  const [searchParams, setSearchParams] =
    useSearchParams<ObservationSearchParams>();

  const [filter, setFilter] = createSignal({ label: "All", key: "search" });
  const [value, setValue] = createSignal(searchParams.search || "");
  const [flyout, setFlyout] = createSignal(false);

  const searchFilters = [
    { label: "All", key: "search" },
    { label: "Observer", key: "observerName" },
    { label: "Category", key: "categoryName" },
    { label: "Location", key: "locationName" },
    { label: "Observed Company", key: "observedCompany" },
  ];

  const emptyParams = {
    search: "",
    observerName: "",
    categoryName: "",
    locationName: "",
    observedCompany: "",
  };

  const clearFilters = () => {
    setSearchParams(emptyParams);
  };

  createEffect(
    on(filter, (f) => {
      const newParams = { ...emptyParams, [f.key]: value() };
      setSearchParams(newParams);
    })
  );

  return (
    <>
      <div class="relative text-left">
        <div class="flex rounded-md shadow-sm">
          <div class="relative flex items-stretch flex-grow focus-within:z-10">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                class="w-5 h-5"
              >
                <path
                  fill-rule="evenodd"
                  d="M9 3.5a5.5 5.5 0 100 11 5.5 5.5 0 000-11zM2 9a7 7 0 1112.452 4.391l3.328 3.329a.75.75 0 11-1.06 1.06l-3.329-3.328A7 7 0 012 9z"
                  clip-rule="evenodd"
                />
              </svg>
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
                  setSearchParams({ [filter().key]: value() });
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
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 20 20"
                    fill="currentColor"
                    class="w-5 h-5"
                  >
                    <path d="M6.28 5.22a.75.75 0 00-1.06 1.06L8.94 10l-3.72 3.72a.75.75 0 101.06 1.06L10 11.06l3.72 3.72a.75.75 0 101.06-1.06L11.06 10l3.72-3.72a.75.75 0 00-1.06-1.06L10 8.94 6.28 5.22z" />
                  </svg>
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
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
              fill="currentColor"
              class="w-5 h-5"
            >
              <path
                fill-rule="evenodd"
                d="M10 3a.75.75 0 01.55.24l3.25 3.5a.75.75 0 11-1.1 1.02L10 4.852 7.3 7.76a.75.75 0 01-1.1-1.02l3.25-3.5A.75.75 0 0110 3zm-3.76 9.2a.75.75 0 011.06.04l2.7 2.908 2.7-2.908a.75.75 0 111.1 1.02l-3.25 3.5a.75.75 0 01-1.1 0l-3.25-3.5a.75.75 0 01.04-1.06z"
                clip-rule="evenodd"
              />
            </svg>
            <div class="truncate sm:max-w-[5rem]">{filter().label}</div>
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
                        checked={filter().key === searchFilter.key}
                        onClick={() => {
                          setFilter(searchFilter);
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
