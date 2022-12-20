import { A, Navigate, useSearchParams } from "@solidjs/router";
import { HTTPError } from "ky";
import { Component, createSignal, Show } from "solid-js";
import { createStore, reconcile } from "solid-js/store";
import { useService } from "solid-services";
import AuthService, { LoginInput } from "../../services/AuthService";

const Login: Component = () => {
  const auth = useService(AuthService);
  const [search] = useSearchParams();

  const [loading, setLoading] = createSignal(false);
  const [errors, setErrors] = createStore({} as Record<string, string>);
  const [credentials, setCredentials] = createStore({} as LoginInput);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setLoading(true);

    setErrors(reconcile({}));

    if (!credentials.email) {
      setErrors({ email: "Please fill in your email." });
    }

    if (!credentials.password) {
      setErrors({ password: "Please fill in your password." });
    }

    if (Object.keys(errors).length > 0) {
      setLoading(false);
      return;
    }

    try {
      await auth().login({
        email: credentials.email,
        password: credentials.password,
      });
    } catch (err: any) {
      if (err instanceof HTTPError) {
        const res = await err.response.json();
        setErrors({ general: "Invalid credentials. Please try again." });
      }

      setLoading(false);
    }
  };

  return (
    <>
      <Show
        when={!auth().state.isLoggedIn}
        fallback={<Navigate href={decodeURI(search.returnTo || "/")} />}
      >
        <div class="min-h-full flex flex-col justify-center py-12 sm:px-6 lg:px-8">
          <div class="sm:mx-auto sm:w-full sm:max-w-md">
            <img
              class="mx-auto h-12 w-auto"
              src="/images/logo.png"
              alt="DASY Logo"
            />
            <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
              Sign in to your account
            </h2>
          </div>

          <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
            <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
              <form class="space-y-6">
                <div>
                  <label
                    for="email"
                    class="block text-sm font-medium text-gray-700"
                  >
                    Email address
                  </label>
                  <div class="mt-1">
                    <input
                      id="email"
                      name="email"
                      type="email"
                      autocomplete="email"
                      required
                      class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-orange-500 focus:border-orange-500 sm:text-sm"
                      classList={{
                        "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500":
                          !!errors.email,
                      }}
                      onInput={(e) => {
                        setErrors({ email: undefined });
                        setCredentials({ email: e.currentTarget.value });
                      }}
                    />
                    <Show when={errors.email}>
                      <p class="mt-2 text-sm text-red-600">{errors.email}</p>
                    </Show>
                  </div>
                </div>

                <div>
                  <label
                    for="password"
                    class="block text-sm font-medium text-gray-700"
                  >
                    Password
                  </label>
                  <div class="mt-1">
                    <input
                      id="password"
                      name="password"
                      type="password"
                      autocomplete="current-password"
                      required
                      class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-orange-500 focus:border-orange-500 sm:text-sm"
                      classList={{
                        "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500":
                          !!errors.password,
                      }}
                      onInput={(e) => {
                        setErrors({ password: undefined });
                        setCredentials({ password: e.currentTarget.value });
                      }}
                    />
                    <Show when={errors.password}>
                      <p class="mt-2 text-sm text-red-600">{errors.password}</p>
                    </Show>
                  </div>
                </div>

                <Show when={errors.general}>
                  <p class="mt-2 text-sm text-red-600">{errors.general}</p>
                </Show>

                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <input
                      id="remember-me"
                      name="remember-me"
                      type="checkbox"
                      class="h-4 w-4 text-orange-600 focus:ring-orange-500 border-gray-300 rounded"
                      onInput={(e) => {
                        setCredentials({ rememberMe: e.currentTarget.checked });
                      }}
                    />
                    <label
                      for="remember-me"
                      class="ml-2 block text-sm text-gray-900"
                    >
                      Remember me
                    </label>
                  </div>

                  <div class="text-sm">
                    <A
                      href="/login/reset-password"
                      class="font-medium text-orange-600 hover:text-orange-500"
                    >
                      Forgot your password?
                    </A>
                  </div>
                </div>

                <div>
                  <button
                    class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                    onClick={submitForm}
                  >
                    {loading() ? "Loading..." : "Sign in"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </Show>
    </>
  );
};

export default Login;
