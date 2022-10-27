import { useSearchParams } from "solid-app-router";
import {
  Component,
  createEffect,
  createResource,
  createSignal,
  Show,
} from "solid-js";
import GeneralFilter from "../../components/filters/GeneralFilter";
import DateFilter from "../../components/filters/DateFilter";
import Pagination from "../../components/Pagination";
import SearchFilter from "../../components/filters/SearchFilter";
import { Page } from "../../models/Common";
import { ObservationDto } from "../../models/Observation";
import { api, Links } from "../../utils/utils";
import ObservationsTable from "./ObservationsTable";
import ColumnSelector from "../../components/filters/ColumnSelector";

type Response = {
  _embedded?: {
    observations: ObservationDto[];
  };
  _links: Links;
  page: Page;
};

export type ObservationSearchParams = {
  // String filters
  search?: string;
  observerName?: string;
  categoryName?: string;
  locationName?: string;
  observerCompany?: string;
  // Collection filters
  status?: string;
  category?: string;
  location?: string;
  immediateDanger?: string;
  // Paging
  page?: string;
  size?: string;
  orderBy?: string;
  orderDir?: "ASC" | "DESC";
};

export const fetchObservations = async (query?: string) => {
  return await api.get("observations" + query).json<Response>();
};

const Observations: Component = () => {
  let previousPage: string | undefined;
  const [queryString, setQueryString] = createSignal("");
  const [searchParams, setSearchParams] =
    useSearchParams<ObservationSearchParams>();
  const [response] = createResource(queryString, fetchObservations);

  createEffect(() => {
    // if the query params changed but the page did not,
    // we updated a filter and have to reset the page
    if (searchParams.page && previousPage === searchParams.page) {
      setSearchParams({ page: undefined });
      return;
    }

    previousPage = searchParams.page;

    let query = "?";
    for (const [key, value] of Object.entries(searchParams)) {
      query += `${key}=${value}&`;
    }

    setQueryString(query);
  });

  return (
    <>
      <div class="flex flex-col sm:flex-row gap-2 mb-4 px-2 sm:px-0">
        <div>
          <SearchFilter />
        </div>
        <div class="flex flex-row gap-2 ml-auto">
          <GeneralFilter />
          <DateFilter />
          <ColumnSelector />
        </div>
      </div>
      <div class="flex flex-col">
        <div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
            <div class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
              <ObservationsTable
                observations={response()?._embedded?.observations ?? []}
              />
              <Show when={response()?.page?.totalElements}>
                <Pagination
                  pageMeta={response()!.page!}
                  onPageChange={(page) => {
                    setSearchParams(
                      { page: page.toString() },
                      { scroll: true }
                    );
                  }}
                ></Pagination>
              </Show>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Observations;
