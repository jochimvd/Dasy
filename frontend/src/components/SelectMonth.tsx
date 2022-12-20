import { For } from "solid-js";

type SelectProps = {
  value?: number;
  setMonth: (id: number) => void;
};

const SelectMonth = (props: SelectProps) => {
  const months = [
    { id: 1, name: "January" },
    { id: 2, name: "February" },
    { id: 3, name: "March" },
    { id: 4, name: "April" },
    { id: 5, name: "May" },
    { id: 6, name: "June" },
    { id: 7, name: "July" },
    { id: 8, name: "August" },
    { id: 9, name: "September" },
    { id: 10, name: "October" },
    { id: 11, name: "November" },
    { id: 12, name: "December", disabled: true },
  ];

  return (
    <>
      <label for="month" class="block text-sm font-medium text-gray-700">
        Month
      </label>
      <select
        id="month"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setMonth(Number(e.currentTarget.value));
        }}
        required
      >
        <For each={months}>
          {(month) => (
            <option
              value={month.id}
              disabled={month.disabled}
              selected={props.value === month.id}
            >
              {month.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectMonth;
