import { Links } from "../utils/utils";

export type SeverityDto = {
  id: number;
  name: string;
  level: number;
  _links?: Links;
};

export type SeverityInput = {
  id?: number;
  name?: string;
  level?: number;
};
