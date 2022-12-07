import { useSearchParams } from "@solidjs/router";
import {
  Component,
  createEffect,
  createResource,
  createSignal,
  Show,
  Suspense,
  untrack,
  useTransition,
} from "solid-js";
import GeneralFilter from "../../components/filters/GeneralFilter";
import DateFilter from "../../components/filters/DateFilter";
import Pagination from "../../components/Pagination";
import SearchFilter from "../../components/filters/SearchFilter";
import ObservationsTable from "./ObservationsTable";
import ColumnSelector from "../../components/filters/ColumnSelector";
import ObservationService from "../../services/ObservationService";

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

const Observations: Component = () => {
  let previousPage: string | undefined;
  const [queryString, setQueryString] = createSignal("");
  const [searchParams, setSearchParams] =
    useSearchParams<ObservationSearchParams>();

  const observationService = ObservationService();
  const [data] = createResource(queryString, observationService.all); // todo use data function
  const observations = () => data()?._embedded?.observations;

  const [, start] = useTransition();

  createEffect(() => {
    const params = untrack(() => searchParams);

    // if the query params changed but the page did not,
    // we updated a filter and have to reset the page
    if (params.page && previousPage === params.page) {
      setSearchParams({ page: undefined });
    }

    previousPage = params.page;

    let query = Object.entries(params)
      .map(([key, value]) => `${key}=${value}`)
      .join("&");

    start(() => setQueryString(query));
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
              {/* <Suspense> */}
              <ObservationsTable
                loading={data.loading}
                observations={observations() ?? []}
              />

              <Show when={data()?.page.totalElements}>
                <Pagination
                  pageMeta={data()!.page}
                  onPageChange={(page) => {
                    setSearchParams(
                      { page: page.toString() },
                      { scroll: true }
                    );
                  }}
                ></Pagination>
              </Show>
              {/* </Suspense> */}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Observations;
