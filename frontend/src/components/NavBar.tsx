import { Link, NavLink } from "solid-app-router";

const NavBar = () => {
  return (
    <>
      <nav class="flex items-center justify-between max-w-3xl p-4 mx-auto">
        <Link
          class="inline-flex items-center justify-center w-10 h-10 bg-gray-100 rounded-lg"
          href="/"
        >
          🦺
        </Link>

        <ul class="flex items-center space-x-2 text-sm font-medium text-gray-500">
          <li class="hidden sm:block">
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/"
              end
            >
              Home
            </NavLink>
          </li>

          <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/observations"
            >
              Observations
            </NavLink>
          </li>

          <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/board"
            >
              Board
            </NavLink>
          </li>

          <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/settings"
            >
              Settings
            </NavLink>
          </li>
          {/* <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/categories"
            >
              {" "}
              Categories{" "}
            </NavLink>
          </li>

          <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/locations"
            >
              {" "}
              Locations{" "}
            </NavLink>
          </li>

          <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/severities"
            >
              {" "}
              Severieties{" "}
            </NavLink>
          </li>

          <li>
            <NavLink
              class="px-3 py-2 rounded-lg hover:bg-gray-200"
              href="/consequences"
            >
              {" "}
              Consequences{" "}
            </NavLink>
          </li> */}
        </ul>
      </nav>
    </>
  );
};

export default NavBar;
