import { NavLink, Outlet } from "solid-app-router";

const SettingsWrapper = () => {
  return (
    <>
      <div class="sm:flex">
        <aside class="w-64" aria-label="Sidebar">
          <div class="overflow-y-auto px-3 rounded">
            <ul class="space-y-2">
              <li>
                <NavLink
                  href="/settings/locations"
                  class="flex items-center p-2 text-base font-normal text-gray-900 rounded-lg hover:bg-gray-200"
                >
                  <span class="flex-1 ml-3 whitespace-nowrap">Locations</span>
                </NavLink>
              </li>
              <li>
                <NavLink
                  href="/settings/severities"
                  class="flex items-center p-2 text-base font-normal text-gray-900 rounded-lg hover:bg-gray-200"
                >
                  <span class="flex-1 ml-3 whitespace-nowrap">Severities</span>
                </NavLink>
              </li>
              <li>
                <NavLink
                  href="/settings/consequences"
                  class="flex items-center p-2 text-base font-normal text-gray-900 rounded-lg hover:bg-gray-200"
                >
                  <span class="flex-1 ml-3 whitespace-nowrap">
                    Consequences
                  </span>
                </NavLink>
              </li>
              <li>
                <NavLink
                  href="/settings/categories"
                  class="flex items-center p-2 text-base font-normal text-gray-900 rounded-lg hover:bg-gray-200"
                >
                  <span class="flex-1 ml-3 whitespace-nowrap">Categories</span>
                </NavLink>
              </li>
            </ul>
          </div>
        </aside>

        <div class="w-full">
          <Outlet />
        </div>
      </div>
    </>
  );
};

export default SettingsWrapper;
