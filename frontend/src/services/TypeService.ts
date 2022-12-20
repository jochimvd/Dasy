import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { TypeDto, TypeInput } from "../models/Type";
import AuthService from "./AuthService";

export type TypeData = ReturnType<ReturnType<typeof TypeService>["typeData"]>;
export type TypesData = ReturnType<ReturnType<typeof TypeService>["typesData"]>;

const TypeService = () => {
  const api = useService(AuthService);

  const Types = {
    all: () => api().send<Collection<"types", TypeDto>>("GET", "types"),
    get: (id: string) => api().send<TypeDto>("GET", `types/${id}`),
    delete: (id: string) => api().send("DELETE", `types/${id}`),
    update: (type: TypeInput) =>
      api().send<TypeDto>("PUT", `types/${type.id}`, type),
    create: (type: TypeInput) => api().send<TypeDto>("POST", "types", type),
  };

  const typeData: RouteDataFunc<unknown, ResourceReturn<TypeDto>> = ({
    params,
  }) => createResource(() => params.id, Types.get);

  const typesData = () => createResource(Types.all);

  return {
    typeData,
    typesData,

    createType(type: TypeInput) {
      return Types.create(type);
    },
    updateType(type: TypeInput) {
      return Types.update(type);
    },
    deleteType(id: string) {
      Types.delete(id);
    },
  };
};

export default TypeService;
