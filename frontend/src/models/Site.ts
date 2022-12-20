import { Links } from "../utils/utils";

export type SiteDto = {
  id: number;
  name: string;
  _links?: Links;
};

export type SiteInput = {
  id?: number;
  name?: string;
};
