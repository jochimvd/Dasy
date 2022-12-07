import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { LocationDto, LocationInput } from "../models/Location";
import AuthService from "./AuthService";

export type LocationData = ReturnType<
  ReturnType<typeof LocationService>["locationData"]
>;
export type LocationsData = ReturnType<
  ReturnType<typeof LocationService>["locationsData"]
>;

const LocationService = () => {
  const api = useService(AuthService);

  const Locations = {
    all: () =>
      api().send<Collection<"locations", LocationDto>>("GET", "locations"),
    get: (id: string) => api().send<LocationDto>("GET", `locations/${id}`),
    delete: (id: string) => api().send("DELETE", `locations/${id}`),
    update: (location: LocationInput) =>
      api().send<LocationDto>("PUT", `locations/${location.id}`, location),
    create: (location: LocationInput) =>
      api().send<LocationDto>("POST", "locations", location),
  };

  const locationData: RouteDataFunc<unknown, ResourceReturn<LocationDto>> = ({
    params,
  }) => createResource(() => params.id, Locations.get);

  const locationsData = () => createResource(Locations.all);

  return {
    locationData,
    locationsData,

    createLocation(location: LocationInput) {
      return Locations.create(location);
    },
    updateLocation(location: LocationInput) {
      return Locations.update(location);
    },
    deleteLocation(id: string) {
      Locations.delete(id);
    },
  };
};

export default LocationService;
