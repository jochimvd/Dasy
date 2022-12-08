import { Show } from "solid-js";
import { CalendarOutline, ChevronDownOutline } from "../../utils/icons";

type SelectProps = {
  value?: number;
  setCategory: (id: number) => void;
};

const DateFilter = () => {
  // const [categories] = createResource(fetchCategories);

  return (
    <>
      <div class="relative inline-block text-left">
        <div>
          <button
            type="button"
            class="whitespace-nowrap inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-orange-500 hover:cursor-not-allowed"
            id="menu-button"
            aria-expanded="true"
            aria-haspopup="true"
          >
            <CalendarOutline class="w-5 h-5 mr-2" />
            Any Date
            <ChevronDownOutline
              class="-mr-1 ml-2 h-5 w-5"
              classList={{ "-rotate-180": false }}
            />
          </button>
        </div>

        {/* <!--
          Dropdown menu, show/hide based on menu state.

          Entering: "transition ease-out duration-100"
            From: "transform opacity-0 scale-95"
            To: "transform opacity-100 scale-100"
          Leaving: "transition ease-in duration-75"
            From: "transform opacity-100 scale-100"
            To: "transform opacity-0 scale-95"
        */}
        <Show when={false}>
          <div
            class="origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
            role="menu"
            aria-orientation="vertical"
            aria-labelledby="menu-button"
            tabindex="-1"
          >
            <div class="py-1" role="none">
              {/* <!-- Active: "bg-gray-100 text-gray-900", Not Active: "text-gray-700" --> */}
              <a
                href="#"
                class="text-gray-700 block px-4 py-2 text-sm"
                role="menuitem"
                tabindex="-1"
                id="menu-item-0"
              >
                Account settings
              </a>
              <a
                href="#"
                class="text-gray-700 block px-4 py-2 text-sm"
                role="menuitem"
                tabindex="-1"
                id="menu-item-1"
              >
                Support
              </a>
              <a
                href="#"
                class="text-gray-700 block px-4 py-2 text-sm"
                role="menuitem"
                tabindex="-1"
                id="menu-item-2"
              >
                License
              </a>
              <form method="post" action="#" role="none">
                <button
                  type="submit"
                  class="text-gray-700 block w-full text-left px-4 py-2 text-sm"
                  role="menuitem"
                  tabindex="-1"
                  id="menu-item-3"
                >
                  Sign out
                </button>
              </form>
            </div>
          </div>
        </Show>
      </div>
    </>
  );
};

export default DateFilter;
