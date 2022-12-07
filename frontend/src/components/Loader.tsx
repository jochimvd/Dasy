import { useIsRouting } from "@solidjs/router";
import { Component, createEffect, createSignal } from "solid-js";

export const [isLoading, setIsLoading] = createSignal(false);

const Loader: Component = () => {
  let timeoutId: number;
  const isRouting = useIsRouting();

  createEffect(() => {
    if (isRouting()) {
      timeoutId = setTimeout(() => {
        setIsLoading(isRouting());
      }, 200);
    } else {
      clearTimeout(timeoutId);
      setIsLoading(isRouting());
    }
  });

  return (
    <div
      class="loader"
      classList={{ active: isLoading(), inactive: !isLoading() }}
    />
  );
};

export default Loader;
