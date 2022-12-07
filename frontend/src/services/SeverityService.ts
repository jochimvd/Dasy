import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { SeverityDto, SeverityInput } from "../models/Severity";
import AuthService from "./AuthService";

export type SeverityData = ReturnType<
  ReturnType<typeof SeverityService>["severityData"]
>;
export type SeveritiesData = ReturnType<
  ReturnType<typeof SeverityService>["severitiesData"]
>;

const SeverityService = () => {
  const api = useService(AuthService);

  const Severities = {
    all: () =>
      api().send<Collection<"severities", SeverityDto>>("GET", "severities"),
    get: (id: string) => api().send<SeverityDto>("GET", `severities/${id}`),
    delete: (id: string) => api().send("DELETE", `severities/${id}`),
    update: (severity: SeverityInput) =>
      api().send<SeverityDto>("PUT", `severities/${severity.id}`, severity),
    create: (severity: SeverityInput) =>
      api().send<SeverityDto>("POST", "severities", severity),
  };

  const severityData: RouteDataFunc<unknown, ResourceReturn<SeverityDto>> = ({
    params,
  }) => createResource(() => params.id, Severities.get);

  const severitiesData = () => createResource(Severities.all);

  return {
    severityData,
    severitiesData,

    createSeverity(severity: SeverityInput) {
      return Severities.create(severity);
    },
    updateSeverity(severity: SeverityInput) {
      return Severities.update(severity);
    },
    deleteSeverity(id: string) {
      Severities.delete(id);
    },
  };
};

export default SeverityService;
