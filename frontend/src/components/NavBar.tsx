import { A, useIsRouting } from "@solidjs/router";
import { createEffect, createSignal, For, Show } from "solid-js";
import { useService } from "solid-services";
import { UserDto } from "../models/User";
import AuthService from "../services/AuthService";
import { clickOutside } from "../utils/Directives";
import { isLoading } from "./Loader";

const navigation = [
  { name: "Home", href: "/", end: true },
  { name: "Observations", href: "/observations" },
  { name: "Board", href: "/board" },
  { name: "Configuration", href: "/configuration" },
];

const userNavigation = [
  { name: "Your Profile", href: "/users/me" },
  { name: "Settings", href: "/settings" },
];

const formatUserInitials = (user?: UserDto) => {
  if (!user) return "UU";
  return (
    user.firstName.charAt(0).toUpperCase() +
    user.lastName.charAt(0).toUpperCase()
  );
};

const formatUserFullName = (user?: UserDto) => {
  if (!user) return "Unknown User";
  return `${user.firstName} ${user.lastName}`;
};

const NavBar = () => {
  const routing = useIsRouting();

  const auth = useService(AuthService);
  const loggedInUser = () => auth().state.user;

  const [mobileNavMenu, setMobileNavMenu] = createSignal(false);
  const [userMenu, setUserMenu] = createSignal(false);

  let userAvatarButton;
  let mobileNavMenuButton: HTMLButtonElement | undefined;
  false && clickOutside;

  createEffect(() => {
    if (!routing()) {
      setMobileNavMenu(false);
    }
  });

  return (
    <>
      <header>
        <nav class="bg-white shadow">
          <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex justify-between h-16">
              <div class="flex">
                <div class="-ml-2 mr-2 flex items-center md:hidden">
                  {/* Mobile menu button */}
                  <button
                    type="button"
                    class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-orange-500"
                    aria-controls="mobile-menu"
                    aria-expanded="false"
                    onclick={() => setMobileNavMenu(!mobileNavMenu())}
                    ref={mobileNavMenuButton}
                  >
                    <span class="sr-only">Open main menu</span>
                    {/*
                      Icon when menu is closed.

                      Heroicon name: outline/menu

                      Menu open: "hidden", Menu closed: "block"
                    */}
                    <svg
                      classList={{
                        hidden: mobileNavMenu(),
                        block: !mobileNavMenu(),
                      }}
                      class="h-6 w-6"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 6h16M4 12h16M4 18h16"
                      />
                    </svg>
                    {/*
                      Icon when menu is open.

                      Heroicon name: outline/x

                      Menu open: "block", Menu closed: "hidden"
                    */}
                    <svg
                      classList={{
                        block: mobileNavMenu(),
                        hidden: !mobileNavMenu(),
                      }}
                      class="h-6 w-6"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M6 18L18 6M6 6l12 12"
                      />
                    </svg>
                  </button>
                </div>
                <div class="flex-shrink-0 flex items-center">
                  <A
                    class="inline-flex items-center justify-center w-8 h-8"
                    href="/"
                  >
                    <img src="/images/logo.png" alt="Safety Logo" />
                  </A>
                </div>
                <div class="hidden md:ml-6 md:flex md:space-x-8">
                  {/* Current: "border-orange-500 text-gray-900", Default: "border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700" */}
                  <For each={navigation}>
                    {(item) => (
                      <A
                        href={item.href}
                        class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
                        activeClass="border-orange-500 text-gray-900"
                        inactiveClass="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700"
                        end={item.end}
                      >
                        {item.name}
                      </A>
                    )}
                  </For>
                </div>
              </div>
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <A
                    href="/observations/create"
                    class="relative inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-orange-600 shadow-sm hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                  >
                    {/* Heroicon name: solid/plus-sm */}
                    <svg
                      class="-ml-1 mr-2 h-5 w-5"
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 20 20"
                      fill="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        fill-rule="evenodd"
                        d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
                        clip-rule="evenodd"
                      />
                    </svg>
                    New Observation
                  </A>
                </div>
                <div class="hidden md:ml-4 md:flex-shrink-0 md:flex md:items-center">
                  <button
                    type="button"
                    class="bg-white p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                  >
                    <span class="sr-only">View notifications</span>
                    {/* Heroicon name: outline/bell */}
                    <svg
                      class="h-6 w-6"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                      />
                    </svg>
                  </button>

                  {/* Profile dropdown */}
                  <div class="ml-3 relative">
                    <div>
                      <button
                        type="button"
                        class="bg-white rounded-full flex text-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                        aria-expanded="false"
                        aria-haspopup="true"
                        onClick={() => setUserMenu(!userMenu())}
                        ref={userAvatarButton}
                      >
                        <span class="sr-only">Open user menu</span>
                        <div class="w-8 h-8 rounded-full flex justify-center items-center bg-orange-100 text-orange-900 font-medium">
                          <span>{formatUserInitials(loggedInUser())}</span>
                        </div>
                      </button>
                    </div>

                    {/*
                      Dropdown menu, show/hide based on menu state.

                      Entering: "transition ease-out duration-200"
                        From: "transform opacity-0 scale-95"
                        To: "transform opacity-100 scale-100"
                      Leaving: "transition ease-in duration-75"
                        From: "transform opacity-100 scale-100"
                        To: "transform opacity-0 scale-95"
                    */}
                    <Show when={userMenu()}>
                      <div
                        class="z-50 origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
                        role="menu"
                        aria-orientation="vertical"
                        aria-labelledby="user-menu-button"
                        tabindex="-1"
                        use:clickOutside={[
                          () => setUserMenu(false),
                          userAvatarButton,
                        ]}
                      >
                        <For each={userNavigation}>
                          {(item) => (
                            <A
                              href={item.href}
                              class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                              activeClass="bg-gray-100"
                              role="menuitem"
                              tabindex="-1"
                              onClick={() => setUserMenu(false)}
                            >
                              {item.name}
                            </A>
                          )}
                        </For>
                        <button
                          class="w-full text-left block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                          role="menuitem"
                          tabindex="-1"
                          onClick={auth().logout}
                        >
                          Sign Out
                        </button>
                      </div>
                    </Show>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Mobile menu, show/hide based on menu state. */}
          <Show when={mobileNavMenu()}>
            <div
              class="md:hidden"
              id="mobile-menu"
              // use:clickOutside={[
              //   () => setMobileNavMenu(false),
              //   mobileNavMenuButton,
              // ]}
            >
              <div class="pt-2 pb-3 space-y-1">
                {/* Current: "bg-orange-50 border-orange-500 text-orange-700", Default: "border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-700" */}
                <For each={navigation}>
                  {(item) => (
                    <A
                      href={item.href}
                      class="block pl-3 pr-4 py-2 border-l-4 text-base font-medium sm:pl-5 sm:pr-6"
                      activeClass="bg-orange-50 border-orange-500 text-orange-700"
                      inactiveClass="border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-700"
                      end={item.end}
                      // onClick={() => setMobileNavMenu(false)}
                    >
                      {item.name}
                    </A>
                  )}
                </For>
              </div>
              <div class="pt-4 pb-3 border-t border-gray-200">
                <div class="flex items-center px-4 sm:px-6">
                  <div class="flex-shrink-0">
                    <div class="w-10 h-10 rounded-full flex justify-center items-center bg-orange-100 text-orange-900 font-medium">
                      <span>{formatUserInitials(loggedInUser())}</span>
                    </div>
                  </div>
                  <div class="ml-3">
                    <div class="text-base font-medium text-gray-800">
                      {formatUserFullName(loggedInUser())}
                    </div>
                    {loggedInUser()?.email && (
                      <div class="text-sm font-medium text-gray-500">
                        {loggedInUser()?.email}
                      </div>
                    )}
                  </div>
                  <button
                    type="button"
                    class="ml-auto flex-shrink-0 bg-white p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                  >
                    <span class="sr-only">View notifications</span>
                    {/* Heroicon name: outline/bell */}
                    <svg
                      class="h-6 w-6"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                      />
                    </svg>
                  </button>
                </div>
                <div class="mt-3 space-y-1">
                  <For each={userNavigation}>
                    {(item) => (
                      <A
                        href={item.href}
                        class="block px-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-100 sm:px-6"
                        activeClass="bg-gray-100"
                      >
                        {item.name}
                      </A>
                    )}
                  </For>
                  <button
                    class="w-full text-left block px-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-100 sm:px-6"
                    onClick={auth().logout}
                  >
                    Sign Out
                  </button>
                </div>
              </div>
            </div>
          </Show>
        </nav>
      </header>
    </>
  );
};

export default NavBar;
