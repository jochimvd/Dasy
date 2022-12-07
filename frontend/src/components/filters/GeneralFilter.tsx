import { createSignal, Show } from "solid-js";
import { Status } from "../../models/Status";
import { ObservationSearchParams } from "../../pages/observations/Observations";
import CategoryFilter from "./CategoryFilter";
import LocationFilter from "./LocationFilter";
import StatusFilter from "./StatusFilter";
import { clickOutside } from "../../utils/Directives";
import DangerFilter from "./DangerFilter";
import { useSearchParams } from "@solidjs/router";

const GeneralFilter = () => {
  let filterButton;
  false && clickOutside; // See: https://github.com/solidjs/solid/issues/1005

  const [flyout, setFlyout] = createSignal(false);
  const [searchParams, setSearchParams] =
    useSearchParams<ObservationSearchParams>();

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
            ref={filterButton}
          >
            <Show
              when={
                searchParams?.category ||
                searchParams?.location ||
                searchParams?.status ||
                searchParams?.immediateDanger
              }
              fallback={
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  class="w-5 h-5 mr-2"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M12 3c2.755 0 5.455.232 8.083.678.533.09.917.556.917 1.096v1.044a2.25 2.25 0 01-.659 1.591l-5.432 5.432a2.25 2.25 0 00-.659 1.591v2.927a2.25 2.25 0 01-1.244 2.013L9.75 21v-6.568a2.25 2.25 0 00-.659-1.591L3.659 7.409A2.25 2.25 0 013 5.818V4.774c0-.54.384-1.006.917-1.096A48.32 48.32 0 0112 3z"
                  />
                </svg>
              }
            >
              {" "}
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                class="w-5 h-5 mr-2"
              >
                <path
                  fill-rule="evenodd"
                  d="M2.628 1.601C5.028 1.206 7.49 1 10 1s4.973.206 7.372.601a.75.75 0 01.628.74v2.288a2.25 2.25 0 01-.659 1.59l-4.682 4.683a2.25 2.25 0 00-.659 1.59v3.037c0 .684-.31 1.33-.844 1.757l-1.937 1.55A.75.75 0 018 18.25v-5.757a2.25 2.25 0 00-.659-1.591L2.659 6.22A2.25 2.25 0 012 4.629V2.34a.75.75 0 01.628-.74z"
                  clip-rule="evenodd"
                />
              </svg>
            </Show>
            Filters
            {/* <!-- Heroicon name: solid/chevron-down --> */}
            <svg
              class="-mr-1 ml-2 h-5 w-5"
              classList={{ "-rotate-180": flyout() }}
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
              fill="currentColor"
              aria-hidden="true"
            >
              <path
                fill-rule="evenodd"
                d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                clip-rule="evenodd"
              />
            </svg>
          </button>
        </div>

        {/*
          Dropdown menu, show/hide based on menu state.

          Entering: "transition ease-out duration-100"
            From: "transform opacity-0 scale-95"
            To: "transform opacity-100 scale-100"
          Leaving: "transition ease-in duration-75"
            From: "transform opacity-100 scale-100"
            To: "transform opacity-0 scale-95"
        */}
        <Show when={flyout()}>
          <div
            class="z-40 origin-top-right absolute left-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
            role="menu"
            aria-orientation="vertical"
            aria-labelledby="menu-button"
            tabindex="-1"
            use:clickOutside={[() => setFlyout(false), filterButton]}
          >
            <div class="py-1" role="none">
              <StatusFilter
                filter={searchParams?.status?.split(",") as Status[]}
                setFilter={(filter) => {
                  setSearchParams({ status: filter });
                }}
              />
              <LocationFilter
                filter={searchParams?.location
                  ?.split(",")
                  .map((c) => parseInt(c))}
                setFilter={(filter) => {
                  setSearchParams({ location: filter });
                }}
              />
              <CategoryFilter
                filter={searchParams?.category
                  ?.split(",")
                  .map((c) => parseInt(c))}
                setFilter={(filter) => {
                  setSearchParams({ category: filter });
                }}
              />
              <DangerFilter
                filter={searchParams?.immediateDanger
                  ?.split(",")
                  .map((c) => c === "true")}
                setFilter={(filter) => {
                  setSearchParams({ immediateDanger: filter });
                }}
              />
            </div>
          </div>
        </Show>
      </div>
    </>
  );
};

export default GeneralFilter;
