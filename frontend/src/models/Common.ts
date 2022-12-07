type Collections =
  | "observations"
  | "categories"
  | "locations"
  | "severities"
  | "consequences"
  | "users";

export type Collection<K extends Collections, T> = {
  _embedded?: {
    [key in K]: T[];
  };
  _links: {
    [key: string]: {
      href: string;
    };
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
};

export type Page = {
  size: number;
  totalElements: number;
  totalPages: number;
  number: number;
};
