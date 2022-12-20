import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { CompanyDto } from "../models/Company";
import AuthService from "./AuthService";

const CompanyService = () => {
  const api = useService(AuthService)();

  const Companies = {
    all: (query?: string) =>
      api.send<Collection<"companies", CompanyDto>>(
        "GET",
        "companies" + (query ? `?${query}` : "")
      ),
    get: (id: string) => api.send<CompanyDto>("GET", `companies/${id}`),
    delete: (id: string) => api.send("DELETE", `companies/${id}`),
  };

  return {
    all(query?: string) {
      return Companies.all(query);
    },

    deleteCompany(id: string) {
      Companies.delete(id);
    },
  };
};

export default CompanyService;
