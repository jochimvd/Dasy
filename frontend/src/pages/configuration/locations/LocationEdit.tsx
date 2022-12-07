import {
  BeforeLeaveEventArgs,
  useBeforeLeave,
  useNavigate,
  useRouteData,
} from "@solidjs/router";
import { Component, createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { LocationInput } from "../../../models/Location";
import LocationService, {
  LocationData,
} from "../../../services/LocationService";

export const LocationEdit: Component<{ new?: boolean }> = (props) => {
  const navigate = useNavigate();

  const locationService = LocationService();
  const [data, { mutate }] = useRouteData<LocationData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<LocationInput>({});
  const updateState = (field: keyof LocationInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const location = await (data()?.id
        ? locationService.updateLocation
        : locationService.createLocation)(state);

      mutate(location);
      navigate("/configuration/locations/" + location.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const location = data();
    if (location && location.id) {
      const { _links, ...rest } = location;
      setState(rest);
    }
  });

  // useBeforeLeave((e: BeforeLeaveEventArgs) => {
  //   if (!saving() && !e.defaultPrevented) {
  //     // TODO: check if form is dirty instead of saving
  //     // preventDefault to block immediately and prompt user async
  //     e.preventDefault();
  //     setTimeout(() => {
  //       if (window.confirm("Discard unsaved changes - are you sure?")) {
  //         // user wants to proceed anyway so retry with force=true
  //         e.retry(true);
  //       }
  //     }, 100);
  //   }
  // });

  return (
    <ConfigurationEditLayout
      heading={"Location"}
      subheading={"Create a new location or edit an existing one."}
      saving={saving}
      onSubmit={submitForm}
    >
      <div class="mt-6 grid grid-cols-4 gap-6">
        <div class="col-span-4 sm:col-span-2">
          <label for="name" class="block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            id="name"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
            placeholder="Location name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default LocationEdit;
