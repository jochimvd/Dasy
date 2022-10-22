import { Links } from "../utils/utils";
import { CategoryDto, CategoryInput } from "./Category";
import { LocationDto, LocationInput } from "./Location";
import { Status } from "./Status";
import { UserDto } from "./User";

export type ObservationDto = {
  id: number;
  key: string;
  observer: UserDto;
  observerCompany: string;
  observedAt: string;
  location: LocationDto;
  observedCompany: string;
  immediateDanger: boolean;
  type: string;
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
  location: LocationInput;
  category: CategoryInput;
  observedCompany: string;
  immediateDanger: boolean;
  description: string;
  actionsTaken: string;
  furtherActions: string;
};
