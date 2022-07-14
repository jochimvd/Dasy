import { Link, useParams } from "solid-app-router";
import { Component, createResource, Suspense } from "solid-js";
import { api, Links } from "../../../utils";
import { Severity } from "../severities/Severity";

export type Category = {
  id?: number;
  name: string;
  consequence: Consequence;
  severity: Severity;
  _links?: Links;
};

type Consequence = {
  id?: number;
  name: string;
  probability: number;
  _links?: Links;
};

export const fetchCategory = async (id: string) => {
  return await api.get(`categories/${id}`).json<Category>();
};

const Category: Component = () => {
  const params = useParams();
  const [category] = createResource(() => params.id, fetchCategory);

  return (
    <>
      <div class="flex mb-5">
        <h1 class="text-xl">Category</h1>

        <Link
          class="ml-4 px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          href="edit"
        >
          Edit
        </Link>
      </div>

      <Suspense fallback={<div>Loading...</div>}>
        <p>
          <strong>Name:</strong> {category()?.name}
          <br />
          <strong>Severity:</strong> {category()?.severity.name}
          <br />
          <strong class="ml-5">Level: </strong>
          {category()?.severity.level}
          <br />
          <strong>Consequence:</strong> {category()?.consequence.name}
          <br />
          <strong class="ml-5">Probability: </strong>
          {category()?.consequence.probability}
        </p>
      </Suspense>
    </>
  );
};

export default Category;
