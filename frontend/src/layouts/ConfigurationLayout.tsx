import { A, Outlet } from "@solidjs/router";
import { For } from "solid-js";

const navigation = [
  { name: "General", href: "/configuration", end: true },
  { name: "Sites", href: "/configuration/sites" },
  { name: "Types", href: "/configuration/types" },
  { name: "Severities", href: "/configuration/severities" },
  { name: "Reoccurrences", href: "/configuration/reoccurrences" },
  { name: "Categories", href: "/configuration/categories" },
];

const ConfigurationLayout = () => {
  return (
    <>
      <div class="lg:grid lg:grid-cols-12 lg:gap-x-5">
        <aside class="pb-6 px-2 lg:py-0 lg:px-0 lg:col-span-3">
          <nav class="space-y-1">
            {/* Current: "bg-gray-50 text-orange-600 hover:bg-white", Default: "text-gray-900 hover:text-gray-900 hover:bg-gray-50" */}
            <For each={navigation}>
              {(item) => (
                <A
                  href={item.href}
                  class="group rounded-md px-3 py-2 flex items-center text-sm font-medium"
                  activeClass="bg-gray-50 text-orange-600 hover:bg-white"
                  inactiveClass="text-gray-900 hover:text-gray-900 hover:bg-gray-50"
                  end={item.end}
                >
                  <span class="truncate"> {item.name} </span>
                </A>
              )}
            </For>
          </nav>
        </aside>
        <div class="lg:col-span-9">
          <Outlet />
        </div>
      </div>
    </>
  );
};

export default ConfigurationLayout;
