import { Links } from "../utils/utils";

export type UserDto = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  company: string;
  _links?: Links;
};
