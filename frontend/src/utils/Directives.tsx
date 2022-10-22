import { Accessor, onCleanup } from "solid-js";

declare module "solid-js" {
  namespace JSX {
    interface Directives {
      clickOutside: [() => void, ...(HTMLElement | undefined)[]]; // use:clickOutside
    }
  }
}

export const clickOutside = (
  element: HTMLElement,
  accessor: Accessor<[() => void, ...(HTMLElement | undefined)[]]>
) => {
  const onClick = (e: MouseEvent) => {
    const target = e.target;
    const [callback, ...exceptions] = accessor();

    target instanceof HTMLElement &&
      [...exceptions, element].every((el) => !el?.contains(target)) &&
      callback();
  };

  document.addEventListener("click", onClick);

  onCleanup(() => document.removeEventListener("click", onClick));
};
