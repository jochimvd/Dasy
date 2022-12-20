import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { SiteDto, SiteInput } from "../models/Site";
import AuthService from "./AuthService";

export type SiteData = ReturnType<ReturnType<typeof SiteService>["siteData"]>;
export type SitesData = ReturnType<ReturnType<typeof SiteService>["sitesData"]>;

const SiteService = () => {
  const api = useService(AuthService);

  const Sites = {
    all: (query?: string) =>
      api().send<Collection<"sites", SiteDto>>(
        "GET",
        "sites" + (query ? `?${query}` : "")
      ),
    get: (id: string) => api().send<SiteDto>("GET", `sites/${id}`),
    delete: (id: string) => api().send("DELETE", `sites/${id}`),
    update: (site: SiteInput) =>
      api().send<SiteDto>("PUT", `sites/${site.id}`, site),
    create: (site: SiteInput) => api().send<SiteDto>("POST", "sites", site),
  };

  const siteData: RouteDataFunc<unknown, ResourceReturn<SiteDto>> = ({
    params,
  }) => createResource(() => params.id, Sites.get);

  const sitesData = () => createResource("", Sites.all);

  return {
    siteData,
    sitesData,

    all(query?: string) {
      return Sites.all(query);
    },

    createSite(site: SiteInput) {
      return Sites.create(site);
    },
    updateSite(site: SiteInput) {
      return Sites.update(site);
    },
    deleteSite(id: string) {
      Sites.delete(id);
    },
  };
};

export default SiteService;
