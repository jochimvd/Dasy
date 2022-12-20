import {
  Component,
  createEffect,
  createSignal,
  For,
  Show,
  splitProps,
  useTransition,
} from "solid-js";
import { CheckS, SelectorS } from "../utils/Icons";
import { clickOutside } from "../utils/Directives";

type ComboBoxProps = {
  id: string;
  label: string;
  wrapperClass?: string;
  placeholder?: string;
  value?: string;
  setValue: (value: string) => void;
  searchTerm?: string;
  setSearchTerm: (value: string) => void;
  options: string[];
  error?: string;
};

const ComboBox: Component<ComboBoxProps> = (props) => {
  let timer: number;
  const waitTime = 400; // Wait time in milliseconds
  let textInput: HTMLInputElement | undefined;
  false && clickOutside;

  const [, start] = useTransition();
  const [active, setActive] = createSignal<number>();
  const [showOptions, setShowOptions] = createSignal(false);

  const select = (index?: number) => {
    if (index !== undefined) {
      textInput?.focus();
      props.setValue(props.options[index]);
    }
    setShowOptions(false);
  };

  const activateAndScroll = (index?: number) => {
    setActive(index);

    if (index !== undefined) {
      const option = document.getElementById(`${props.id}-option-${index}`);
      option?.scrollIntoView({ block: "nearest" });
    }
  };

  createEffect(() => {
    if (showOptions() && props.options && props.searchTerm) {
      activateAndScroll(0);
    } else {
      setActive(undefined);
    }
  });

  // const [local, others] = splitProps(props, ["label", "wrapperClass", "options"]);

  return (
    <div
      class={props.wrapperClass}
      use:clickOutside={[() => setShowOptions(false)]}
    >
      <label for={props.id} class="block text-sm font-medium text-gray-700">
        {props.label}
      </label>
      <div class="relative mt-1">
        <input
          id={props.id}
          type="text"
          class="w-full rounded-md border border-gray-300 bg-white py-2 pl-3 pr-12 shadow-sm focus:border-black focus:outline-none focus:ring-1 focus:ring-black sm:text-sm"
          classList={{
            "border-red-300 text-red-900 placeholder-red-300 focus:ring-red-500 focus:border-red-500":
              !!props.error,
          }}
          role="combobox"
          aria-controls="options"
          aria-expanded="false"
          ref={textInput}
          placeholder={props.placeholder}
          value={props.value ?? ""}
          onKeyDown={(e) => {
            let activeIndex = active();

            if (e.key === "Tab") {
              setShowOptions(false);
              return;
            }

            if (e.key === "Enter") {
              e.preventDefault();
              select(activeIndex);
              return;
            }

            if (e.key === "Escape") {
              e.preventDefault();
              setShowOptions(false);
              return;
            }

            if (e.key === "ArrowDown") {
              if (activeIndex === undefined) {
                activeIndex = 0;
              } else {
                activeIndex = (activeIndex + 1) % props.options.length;
              }
            }
            if (e.key === "ArrowUp") {
              if (activeIndex === undefined) {
                activeIndex = props.options.length - 1;
              } else {
                activeIndex =
                  (activeIndex - 1 + props.options.length) %
                  props.options.length;
              }
            }

            activateAndScroll(activeIndex);
          }}
          onInput={(e) => {
            props.setValue(e.currentTarget.value);

            // Clear timer
            clearTimeout(timer);

            // Wait for waitTime ms and then send the search term
            timer = setTimeout(() => {
              start(() => {
                setShowOptions(true);
                props.setSearchTerm(props.value?.trim() ?? "");
              });
            }, waitTime);
          }}
        />
        <button
          type="button"
          class="absolute inset-y-0 right-0 flex items-center rounded-r-md px-2 focus:outline-none focus:ring-2 focus:ring-gray-900"
          onClick={() => {
            textInput?.focus();
            start(() => {
              // Show all options when opening the dropdown with this button
              // previous state was false, hence checking for !showOptions()
              if (!showOptions()) {
                props.setSearchTerm("");
              }
              setShowOptions(!showOptions());
            });
          }}
        >
          <SelectorS />
        </button>

        <Show when={showOptions() && props.options?.length > 0}>
          <ul
            class="absolute z-10 mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm"
            id="options"
            role="listbox"
          >
            <For each={props.options}>
              {(option, index) => {
                return (
                  <li
                    class="relative cursor-default select-none py-2 pl-3 pr-9"
                    classList={{
                      "text-white bg-orange-600": active() === index(),
                      "text-gray-900": active() !== index(),
                    }}
                    id={`${props.id}-option-${index()}`}
                    role="option"
                    tabindex="-1"
                    onMouseEnter={(e) => {
                      activateAndScroll(index());
                    }}
                    onMouseLeave={() => setActive(undefined)}
                    onClick={() => select(index())}
                  >
                    <span
                      class="block truncate"
                      classList={{ "font-semibold": props.value === option }}
                    >
                      {option}
                    </span>
                  </li>
                );
              }}
            </For>
          </ul>
        </Show>
      </div>
      <Show when={props.error}>
        <p class="mt-2 text-sm text-red-600">{props.error}</p>
      </Show>
    </div>
  );
};

export default ComboBox;
