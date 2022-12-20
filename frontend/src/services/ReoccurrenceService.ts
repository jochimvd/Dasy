import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { ReoccurrenceDto, ReoccurrenceInput } from "../models/Reoccurrence";
import AuthService from "./AuthService";

export type ReoccurrenceData = ReturnType<
  ReturnType<typeof ReoccurrenceService>["reoccurrenceData"]
>;
export type ReoccurrencesData = ReturnType<
  ReturnType<typeof ReoccurrenceService>["reoccurrencesData"]
>;

const ReoccurrenceService = () => {
  const api = useService(AuthService);

  const Reoccurrences = {
    all: () =>
      api().send<Collection<"reoccurrences", ReoccurrenceDto>>(
        "GET",
        "reoccurrences"
      ),
    get: (id: string) =>
      api().send<ReoccurrenceDto>("GET", `reoccurrences/${id}`),
    delete: (id: string) => api().send("DELETE", `reoccurrences/${id}`),
    update: (reoccurrence: ReoccurrenceInput) =>
      api().send<ReoccurrenceDto>(
        "PUT",
        `reoccurrences/${reoccurrence.id}`,
        reoccurrence
      ),
    create: (reoccurrence: ReoccurrenceInput) =>
      api().send<ReoccurrenceDto>("POST", "reoccurrences", reoccurrence),
  };

  const reoccurrenceData: RouteDataFunc<
    unknown,
    ResourceReturn<ReoccurrenceDto>
  > = ({ params }) => createResource(() => params.id, Reoccurrences.get);

  const reoccurrencesData = () => createResource(Reoccurrences.all);

  return {
    reoccurrenceData,
    reoccurrencesData,

    createReoccurrence(reoccurrence: ReoccurrenceInput) {
      return Reoccurrences.create(reoccurrence);
    },
    updateReoccurrence(reoccurrence: ReoccurrenceInput) {
      return Reoccurrences.update(reoccurrence);
    },
    deleteReoccurrence(id: string) {
      Reoccurrences.delete(id);
    },
  };
};

export default ReoccurrenceService;
