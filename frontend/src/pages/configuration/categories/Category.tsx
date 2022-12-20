import { useNavigate, useRouteData } from "@solidjs/router";
import { Component } from "solid-js";
import ConfigurationDetailLayout from "../../../layouts/ConfigurationDetailLayout";
import CategoryService, {
  CategoryData,
} from "../../../services/CategoryService";

const Category: Component = () => {
  const navigate = useNavigate();
  const categoryService = CategoryService();
  const [category] = useRouteData<CategoryData>();

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this category?")) {
      categoryService.deleteCategory(category()!.id.toString());
      navigate("/configuration/categories");
    }
  };

  return (
    <ConfigurationDetailLayout
      heading="Category"
      actions={{
        edit: category()?._links?.put ? () => navigate("edit") : undefined,
        delete: category()?._links?.delete ? handleDelete : undefined,
      }}
    >
      <p>
        <strong>Name:</strong> {category()?.name}
        <br />
        <strong>Severity level:</strong> {category()?.severityLevel}
        <br />
        <strong>Reoccurrence:</strong> {category()?.reoccurrence.name}
        <br />
        <strong class="ml-5">Rate: </strong>
        {category()?.reoccurrence.rate}
      </p>
    </ConfigurationDetailLayout>
  );
};

export default Category;
