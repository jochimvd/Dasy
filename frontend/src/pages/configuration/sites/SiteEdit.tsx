import {
  BeforeLeaveEventArgs,
  useBeforeLeave,
  useNavigate,
  useRouteData,
} from "@solidjs/router";
import { Component, createComputed, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import ConfigurationEditLayout from "../../../layouts/ConfigurationEditLayout";
import { SiteInput } from "../../../models/Site";
import SiteService, { SiteData } from "../../../services/SiteService";

export const SiteEdit: Component<{ new?: boolean }> = (props) => {
  const navigate = useNavigate();

  const siteService = SiteService();
  const [data, { mutate }] = useRouteData<SiteData>();
  const [saving, setSaving] = createSignal(false);

  const [state, setState] = createStore<SiteInput>({});
  const updateState = (field: keyof SiteInput) => (e: Event) =>
    setState(field, (e.target as HTMLInputElement).value);

  const submitForm = async (e: Event) => {
    e.preventDefault();
    setSaving(true);

    try {
      const site = await (data()?.id
        ? siteService.updateSite
        : siteService.createSite)(state);

      mutate(site);
      navigate("/configuration/sites/" + site.id);
    } catch (e) {
      setSaving(false);
    }
  };

  createComputed(() => {
    const sites = data();
    if (sites && sites.id) {
      const { _links, ...rest } = sites;
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
      heading={"Site"}
      subheading={"Create a new site or edit an existing one."}
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
            placeholder="Site name"
            value={state.name ?? ""}
            onInput={updateState("name")}
          />
        </div>
      </div>
    </ConfigurationEditLayout>
  );
};

export default SiteEdit;
