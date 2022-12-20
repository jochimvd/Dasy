import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { CategoryReport, MonthReport, SiteReport } from "../models/Report";
import AuthService from "./AuthService";

const ReportService = () => {
  const api = useService(AuthService);

  const Reports = {
    category: (monthReport: MonthReport) =>
      api().send<Collection<"categories", CategoryReport>>(
        "POST",
        `reports/categories/month`,
        monthReport
      ),
    site: (monthReport: MonthReport) =>
      api().send<Collection<"sites", SiteReport>>(
        "POST",
        `reports/sites/month`,
        monthReport
      ),
  };

  return {
    categoryReport(monthReport: MonthReport) {
      return Reports.category(monthReport);
    },
    siteReport(monthReport: MonthReport) {
      return Reports.site(monthReport);
    },
  };
};

export default ReportService;
