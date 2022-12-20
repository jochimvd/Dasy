import { createSignal } from "solid-js";
import ConfigurationEditLayout from "../../layouts/ConfigurationEditLayout";

const General = () => {
  const [saving, setSaving] = createSignal(false);

  return (
    <ConfigurationEditLayout
      heading={"General Configuration"}
      subheading={"Update the applications' general configuration."}
      saving={saving}
      onSubmit={() => {}}
    >
      <div class="mt-6 grid grid-cols-4 gap-6">
        <div class="col-span-4 sm:col-span-2">
          <label
            for="observation-start"
            class="block text-sm font-medium text-gray-700"
          >
            Start of observations
          </label>
          <input
            id="observation-start"
            type="datetime-local"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            value={"2022-05-01T00:00:00"}
          />
        </div>
      </div>

      <div class="mt-6 grid grid-cols-4 gap-6">
        <div class="col-span-4 sm:col-span-2">
          <label
            for="notification-threshold"
            class="block text-sm font-medium text-gray-700"
          >
            Notification threshold
          </label>
          <input
            id="notification-threshold"
            type="number"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            value={"20"}
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default General;
