import { Links } from "../utils/utils";
import { ReoccurrenceDto, ReoccurrenceInput } from "./Reoccurrence";

export type CategoryDto = {
  id: number;
  name: string;
  severityLevel: number;
  reoccurrence: ReoccurrenceDto;
  _links?: Links;
};

export type CategoryInput = {
  id?: number;
  name?: string;
  severityLevel?: number;
  reoccurrence?: ReoccurrenceInput;
};
