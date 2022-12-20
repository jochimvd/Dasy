import { Links } from "../utils/utils";

export type CompanyDto = {
  id: number;
  name: string;
  _links?: Links;
};
