import { Links } from "../utils/utils";

export type LocationDto = {
  id: number;
  name: string;
  _links?: Links;
};

export type LocationInput = {
  id?: number;
  name?: string;
};
