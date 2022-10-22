import { Link, useParams } from "solid-app-router";
import { Component, createResource, Suspense } from "solid-js";
import { SeverityDto } from "../../../models/Severity";
import { api } from "../../../utils/utils";

export const fetchSeverity = async (id: string) => {
  return await api.get(`severities/${id}`).json<SeverityDto>();
};

const Severity: Component = () => {
  const params = useParams();
  const [severity] = createResource(() => params.id, fetchSeverity);

  return (
    <>
      <div class="flex mb-5">
        <h1 class="text-xl">Severity</h1>

        <Link
          class="ml-4 px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          href="edit"
        >
          Edit
        </Link>
      </div>

      <Suspense fallback={<div>Loading...</div>}>
        <p>
          <strong>Name:</strong> {severity()?.name}
          <br />
          <strong>Level:</strong> {severity()?.level}
        </p>
      </Suspense>
    </>
  );
};

export default Severity;
