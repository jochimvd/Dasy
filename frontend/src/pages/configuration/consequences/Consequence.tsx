import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import ConsequenceService, {
  ConsequenceData,
} from "../../../services/ConsequenceService";

const Consequence: Component = () => {
  const navigate = useNavigate();
  const consequenceService = ConsequenceService();
  const [consequence] = useRouteData<ConsequenceData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this consequence?")) {
      consequenceService.deleteConsequence(consequence()!.id.toString());
      navigate("/configuration/consequences");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Consequence"
      actions={{
        edit: consequence()?._links?.put ? () => navigate("edit") : undefined,
        delete: consequence()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {consequence()?.name}
        <br />
        <strong>Probability:</strong> {consequence()?.probability}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Consequence;
