import { Links } from "../utils/utils";

export type ConsequenceDto = {
  id: number;
  name: string;
  probability: number;
  _links?: Links;
};

export type ConsequenceInput = {
  id?: number;
  name?: string;
  probability?: number;
};
