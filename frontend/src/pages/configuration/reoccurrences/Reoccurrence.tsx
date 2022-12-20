import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import ReoccurrenceService, {
  ReoccurrenceData,
} from "../../../services/ReoccurrenceService";

const Reoccurrence: Component = () => {
  const navigate = useNavigate();
  const reoccurrenceService = ReoccurrenceService();
  const [reoccurrence] = useRouteData<ReoccurrenceData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this reoccurrence?")) {
      reoccurrenceService.deleteReoccurrence(reoccurrence()!.id.toString());
      navigate("/configuration/reoccurrences");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Reoccurrence"
      actions={{
        edit: reoccurrence()?._links?.put ? () => navigate("edit") : undefined,
        delete: reoccurrence()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {reoccurrence()?.name}
        <br />
        <strong>Rate:</strong> {reoccurrence()?.rate}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Reoccurrence;
