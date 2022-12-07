import { RouteDataFunc } from "@solidjs/router";
import { createResource, ResourceReturn } from "solid-js";
import { useService } from "solid-services";
import { Collection } from "../models/Common";
import { CategoryDto, CategoryInput } from "../models/Category";
import AuthService from "./AuthService";

export type CategoryData = ReturnType<
  ReturnType<typeof CategoryService>["categoryData"]
>;
export type CategoriesData = ReturnType<
  ReturnType<typeof CategoryService>["categoriesData"]
>;

const CategoryService = () => {
  const api = useService(AuthService);

  const Categories = {
    all: () =>
      api().send<Collection<"categories", CategoryDto>>("GET", "categories"),
    get: (id: string) => api().send<CategoryDto>("GET", `categories/${id}`),
    delete: (id: string) => api().send("DELETE", `categories/${id}`),
    update: (category: CategoryInput) =>
      api().send<CategoryDto>("PUT", `categories/${category.id}`, category),
    create: (category: CategoryInput) =>
      api().send<CategoryDto>("POST", "categories", category),
  };

  const categoryData: RouteDataFunc<unknown, ResourceReturn<CategoryDto>> = ({
    params,
  }) => createResource(() => params.id, Categories.get);

  const categoriesData = () => createResource(Categories.all);

  return {
    categoryData,
    categoriesData,

    createCategory(category: CategoryInput) {
      return Categories.create(category);
    },
    updateCategory(category: CategoryInput) {
      return Categories.update(category);
    },
    deleteCategory(id: string) {
      Categories.delete(id);
    },
  };
};

export default CategoryService;
