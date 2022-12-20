import { Links } from "../utils/utils";

export type ReoccurrenceDto = {
  id: number;
  name: string;
  rate: number;
  _links?: Links;
};

export type ReoccurrenceInput = {
  id?: number;
  name?: string;
  rate?: number;
};
