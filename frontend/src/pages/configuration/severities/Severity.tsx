import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import SeverityService, {
  SeverityData,
} from "../../../services/SeverityService";

const Severity: Component = () => {
  const navigate = useNavigate();
  const severityService = SeverityService();
  const [severity] = useRouteData<SeverityData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this severity?")) {
      severityService.deleteSeverity(severity()!.id.toString());
      navigate("/configuration/severities");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Severity"
      actions={{
        edit: severity()?._links?.put ? () => navigate("edit") : undefined,
        delete: severity()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {severity()?.name}
        <br />
        <strong>Level:</strong> {severity()?.level}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Severity;
