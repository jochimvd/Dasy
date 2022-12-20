import { A, Navigate, useNavigate, useSearchParams } from "@solidjs/router";
import { Component, createSignal, Show } from "solid-js";
import { createStore } from "solid-js/store";
import { useService } from "solid-services";
import AuthService from "../../services/AuthService";

const ResetPassword: Component = () => {
  const navigate = useNavigate();
  const auth = useService(AuthService);
  const [search] = useSearchParams();

  const [loading, setLoading] = createSignal(false);
  const [errors, setErrors] = createStore({} as Record<string, string>);
  const [email, setEmail] = createSignal("");

  const submitForm = (e: Event) => {
    e.preventDefault();
    setLoading(true);

    setErrors({});

    if (!email()) {
      setErrors({ email: "Please fill in your email." });
      setLoading(false);
      return;
    }

    setTimeout(() => {
      navigate("/login");
    }, 1000);
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
              Reset your password
            </h2>
          </div>

          <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
            <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 divide-y divide-gray-200">
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
                      placeholder="Enter email"
                      class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-orange-500 focus:border-orange-500 sm:text-sm"
                      classList={{
                        "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500":
                          !!errors.email,
                      }}
                      onInput={(e) => {
                        setErrors({ email: undefined });
                        setEmail(e.currentTarget.value);
                      }}
                    />
                    <Show when={errors.email}>
                      <p class="mt-2 text-sm text-red-600">{errors.email}</p>
                    </Show>
                  </div>
                </div>

                <div>
                  <button
                    class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                    onClick={submitForm}
                  >
                    {loading() ? "Loading..." : "Reset Password"}
                  </button>
                </div>
              </form>

              <div class="mt-6">
                <p class="mt-4 text-center">
                  <A
                    href="/login"
                    class="font-medium text-orange-600 hover:text-orange-500"
                  >
                    Return to log in
                  </A>
                </p>
              </div>
            </div>
          </div>
        </div>
      </Show>
    </>
  );
};

export default ResetPassword;
