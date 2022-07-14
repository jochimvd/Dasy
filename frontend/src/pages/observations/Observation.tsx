import { useParams } from "solid-app-router";
import { Component, createResource, Suspense } from "solid-js";
import { api, Links } from "../../utils";
import { Category } from "../settings/categories/Category";

export type Status = "NEW" | "IN_PROGRESS" | "DONE";

export type Observation = {
  id?: number;
  key: string;
  observer: string;
  observerCompany: string;
  observedAt: string;
  location: Location;
  observedCompany: string;
  immediateDanger: boolean;
  type: string;
  category: Category;
  description: string;
  actionsTaken: string;
  furtherActions: string;
  status: Status;
  _links: Links;
};

type Location = {
  id?: number;
  name: string;
};

const fetchObservation = async (id: string) => {
  return await api.get(`observations/${id}`).json<Observation>();
};

const Observation: Component = () => {
  const params = useParams();
  const [observation] = createResource(() => params.id, fetchObservation);

  return (
    <>
      <h1 class="text-xl mb-5">Observation</h1>

      <Suspense fallback={<div>Loading...</div>}>
        <table>
          <tbody>
            <tr>
              <td>Key</td> <td>{observation()?.key}</td>
            </tr>
            <tr>
              <td>Observer</td> <td>{observation()?.observer}</td>
            </tr>
            <tr>
              <td>Observer Company</td>{" "}
              <td>{observation()?.observerCompany}</td>
            </tr>
            <tr>
              <td>Observed At</td>{" "}
              <td>
                {new Date(observation()?.observedAt ?? "").toLocaleString()}
              </td>
            </tr>
            <tr>
              <td>Location</td> <td>{observation()?.location.name}</td>
            </tr>
            <tr>
              <td>Observed Company</td>{" "}
              <td>{observation()?.observedCompany}</td>
            </tr>
            <tr>
              <td>Immediate Danger</td>{" "}
              <td>{observation()?.immediateDanger ? "Yes" : "No"}</td>
            </tr>
            <tr>
              <td>Type</td> <td>{observation()?.type}</td>
            </tr>
            <tr>
              <td>Category</td> <td>{observation()?.category.name}</td>
            </tr>
            <tr>
              <td>Description</td> <td>{observation()?.description}</td>
            </tr>
            <tr>
              <td>Actions Taken</td> <td>{observation()?.actionsTaken}</td>
            </tr>
            <tr>
              <td>Further Actions</td> <td>{observation()?.furtherActions}</td>
            </tr>
            <tr>
              <td>Status</td> <td>{observation()?.status}</td>
            </tr>
          </tbody>
        </table>
      </Suspense>
    </>
  );
};

export default Observation;
