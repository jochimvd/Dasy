import { CategoryDto } from "./Category";
import { SiteDto } from "./Site";

export type MonthReport = {
  month: number;
  year: number;
};

export type SiteReport = {
  site: SiteDto;
  observations: number;
  positiveObservations: number;
};

export type CategoryReport = {
  category: CategoryDto;
  score: number;
};
