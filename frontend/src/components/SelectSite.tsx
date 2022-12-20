import { For } from "solid-js";
import SiteService from "../services/SiteService";

type SelectProps = {
  value?: number;
  setSite: (id: number) => void;
};

const SelectSite = (props: SelectProps) => {
  const [data] = SiteService().sitesData();
  const sites = () => data()?._embedded?.sites;

  return (
    <>
      <label for="site" class="block text-sm font-medium text-gray-700">
        Site
      </label>
      <select
        id="site"
        class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
        onInput={(e) => {
          props.setSite(Number(e.currentTarget.value));
        }}
        required
      >
        <option>Select a site</option>
        <For each={sites()?.sort((a, b) => a.name.localeCompare(b.name))}>
          {(site) => (
            <option value={site.id} selected={props.value === site.id}>
              {site.name}
            </option>
          )}
        </For>
      </select>
    </>
  );
};

export default SelectSite;
