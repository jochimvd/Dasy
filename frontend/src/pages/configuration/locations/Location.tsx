import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import LocationService, {
  LocationData,
} from "../../../services/LocationService";

const Location: Component = () => {
  const navigate = useNavigate();
  const locationService = LocationService();
  const [location] = useRouteData<LocationData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this location?")) {
      locationService.deleteLocation(location()!.id.toString());
      navigate("/configuration/locations");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Location"
      actions={{
        edit: location()?._links?.put ? () => navigate("edit") : undefined,
        delete: location()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {location()?.name}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Location;
