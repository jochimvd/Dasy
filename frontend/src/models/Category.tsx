import { Links } from "../utils/utils";
import { ConsequenceDto, ConsequenceInput } from "./Consequence";
import { SeverityDto, SeverityInput } from "./Severity";

export type CategoryDto = {
  id: number;
  name: string;
  consequence: ConsequenceDto;
  severity: SeverityDto;
  _links?: Links;
};

export type CategoryInput = {
  id?: number;
  name?: string;
  severity?: SeverityInput;
  consequence?: ConsequenceInput;
};
