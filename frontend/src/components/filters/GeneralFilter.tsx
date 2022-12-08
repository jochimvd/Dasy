import { createSignal, Show } from "solid-js";
import { Status } from "../../models/Status";
import { ObservationSearchParams } from "../../pages/observations/Observations";
import CategoryFilter from "./CategoryFilter";
import LocationFilter from "./LocationFilter";
import StatusFilter from "./StatusFilter";
import { clickOutside } from "../../utils/Directives";
import DangerFilter from "./DangerFilter";
import { useSearchParams } from "@solidjs/router";
import {
  ChevronDownOutline,
  FunnelOutline,
  FunnelSolid,
} from "../../utils/icons";

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
              fallback={<FunnelOutline class="w-5 h-5 mr-2" />}
            >
              <FunnelSolid class="w-5 h-5 mr-2" />
            </Show>
            Filters
            <ChevronDownOutline
              class="-mr-1 ml-2 h-5 w-5"
              classList={{ "-rotate-180": flyout() }}
            />
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
