import { RouteDataFunc, RouteDataFuncArgs } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { ObservationDto, ObservationInput } from "../models/Observation";
import AuthService from "./AuthService";

export type ObservationData = ReturnType<
  ReturnType<typeof ObservationService>["observationData"]
>;
export type ObservationsData = ReturnType<
  ReturnType<typeof ObservationService>["observationsData"]
>;

const ObservationService = () => {
  const api = useService(AuthService);

  const Observations = {
    all: (query?: string) =>
      api().send<Collection<"observations", ObservationDto>>(
        "GET",
        "observations" + (query ? `?${query}` : "")
      ),
    get: (id: string) =>
      api().send<ObservationDto>("GET", `observations/${id}`),
    delete: (id: string) => api().send<void>("DELETE", `observations/${id}`),
    update: (observation: ObservationInput) =>
      api().send<ObservationDto>(
        "PUT",
        `observations/${observation.id}`,
        observation
      ),
    create: (observation: ObservationInput) =>
      api().send<ObservationDto>("POST", "observations", observation),
    patchStatus(id: string, status: string) {
      return api().send<ObservationDto>("PATCH", `observations/${id}`, [
        { op: "replace", path: "/status", value: status },
      ]);
    },
  };

  const observationData: RouteDataFunc<
    unknown,
    ResourceReturn<ObservationDto>
  > = ({ params }) => createResource(() => params.id, Observations.get);

  const observationsData = () => createResource("", Observations.all);

  return {
    observationData,
    observationsData,

    all(query?: string) {
      return Observations.all(query);
    },

    createObservation(observation: ObservationInput) {
      return Observations.create(observation);
    },
    updateObservation(observation: ObservationInput) {
      return Observations.update(observation);
    },

    async patchObservation(observation: ObservationDto) {
      return await Observations.patchStatus(
        observation.id.toString(),
        observation.status
      );
    },
    deleteObservation(id: string) {
      Observations.delete(id);
    },
  };
};

export default ObservationService;
