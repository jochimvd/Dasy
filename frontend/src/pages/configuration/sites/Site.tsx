import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import SiteService, { SiteData } from "../../../services/SiteService";

const Site: Component = () => {
  const navigate = useNavigate();
  const siteService = SiteService();
  const [site] = useRouteData<SiteData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this site?")) {
      siteService.deleteSite(site()!.id.toString());
      navigate("/configuration/sites");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Site"
      actions={{
        edit: site()?._links?.put ? () => navigate("edit") : undefined,
        delete: site()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {site()?.name}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Site;
