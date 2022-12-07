import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { ConsequenceDto, ConsequenceInput } from "../models/Consequence";
import AuthService from "./AuthService";

export type ConsequenceData = ReturnType<
  ReturnType<typeof ConsequenceService>["consequenceData"]
>;
export type ConsequencesData = ReturnType<
  ReturnType<typeof ConsequenceService>["consequencesData"]
>;

const ConsequenceService = () => {
  const api = useService(AuthService);

  const Consequences = {
    all: () =>
      api().send<Collection<"consequences", ConsequenceDto>>(
        "GET",
        "consequences"
      ),
    get: (id: string) =>
      api().send<ConsequenceDto>("GET", `consequences/${id}`),
    delete: (id: string) => api().send("DELETE", `consequences/${id}`),
    update: (consequence: ConsequenceInput) =>
      api().send<ConsequenceDto>(
        "PUT",
        `consequences/${consequence.id}`,
        consequence
      ),
    create: (consequence: ConsequenceInput) =>
      api().send<ConsequenceDto>("POST", "consequences", consequence),
  };

  const consequenceData: RouteDataFunc<
    unknown,
    ResourceReturn<ConsequenceDto>
  > = ({ params }) => createResource(() => params.id, Consequences.get);

  const consequencesData = () => createResource(Consequences.all);

  return {
    consequenceData,
    consequencesData,

    createConsequence(consequence: ConsequenceInput) {
      return Consequences.create(consequence);
    },
    updateConsequence(consequence: ConsequenceInput) {
      return Consequences.update(consequence);
    },
    deleteConsequence(id: string) {
      Consequences.delete(id);
    },
  };
};

export default ConsequenceService;
