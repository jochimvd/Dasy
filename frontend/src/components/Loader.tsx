import { useIsRouting } from "@solidjs/router";
import { Component, createEffect, createSignal } from "solid-js";

const [isLoading, setIsLoading] = createSignal(false);
export const loadingIndicator = (isLoading: boolean) => setIsLoading(isLoading);

const Loader: Component = () => {
  let timeoutId: number;
  const isRouting = useIsRouting();
  const [loadingIndicator, setLoadingIndicator] = createSignal(false);

  createEffect(() => {
    const loading = isRouting() || isLoading();

    if (loading) {
      timeoutId = setTimeout(() => {
        setLoadingIndicator(loading);
      }, 200);
    } else {
      clearTimeout(timeoutId);
      setLoadingIndicator(loading);
    }
  });

  return (
    <div
      class="loader"
      classList={{ active: loadingIndicator(), inactive: !loadingIndicator() }}
    />
  );
};

export default Loader;
