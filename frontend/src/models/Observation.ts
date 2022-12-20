import { Links } from "../utils/utils";
import { CategoryDto, CategoryInput } from "./Category";
import { SiteDto, SiteInput } from "./Site";
import { Status } from "./Status";
import { TypeDto, TypeInput } from "./Type";
import { UserDto } from "./User";

export type ObservationDto = {
  id: number;
  key: string;
  observer: UserDto;
  observedAt: string;
  site: string;
  observedCompany: string;
  immediateDanger: boolean;
  type: TypeDto;
  category: CategoryDto;
  description: string;
  actionsTaken: string;
  furtherActions: string;
  status: Status;
  _links?: Links;
};

export type ObservationInput = {
  id?: number;
  observedAt?: string;
  site?: string;
  category?: CategoryInput;
  observedCompany?: string;
  immediateDanger?: boolean;
  type?: TypeInput;
  description?: string;
  actionsTaken?: string;
  furtherActions?: string;
};
