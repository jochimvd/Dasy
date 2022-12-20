import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import TypeService, { TypeData } from "../../../services/TypeService";

const Type: Component = () => {
  const navigate = useNavigate();
  const typeService = TypeService();
  const [type] = useRouteData<TypeData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this type?")) {
      typeService.deleteType(type()!.id.toString());
      navigate("/configuration/types");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Type"
      actions={{
        edit: type()?._links?.put ? () => navigate("edit") : undefined,
        delete: type()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {type()?.name}
        <br />
        <strong>Notify:</strong> {type()?.notify ? "Yes" : "No"}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Type;
