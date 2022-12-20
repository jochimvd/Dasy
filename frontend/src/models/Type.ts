import { Links } from "../utils/utils";

export type TypeDto = {
  id: number;
  name: string;
  notify: boolean;
  _links?: Links;
};

export type TypeInput = {
  id?: number;
  name?: string;
  notify?: boolean;
};
