import { useSearchParams } from "@solidjs/router";
import {
  Component,
  createEffect,
  createResource,
  createSignal,
  Show,
  untrack,
  useTransition,
} from "solid-js";
import ColumnSelector from "../../components/filters/ColumnSelector";
import DateFilter from "../../components/filters/DateFilter";
import GeneralFilter from "../../components/filters/GeneralFilter";
import SearchFilter from "../../components/filters/SearchFilter";
import Pagination from "../../components/Pagination";
import ObservationService from "../../services/ObservationService";
import { CubeOutline } from "../../utils/Icons";
import ObservationsTable from "./ObservationsTable";

export type ObservationSearchParams = {
  // String filters
  search?: string;
  observerName?: string;
  categoryName?: string;
  siteName?: string;
  observerCompany?: string;
  // Collection filters
  status?: string;
  category?: string;
  site?: string;
  type?: string;
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
  const [data] = createResource(queryString, observationService.all); // TODO: use data function
  const observations = () => data()?._embedded?.observations;

  const [pending, start] = useTransition();

  // createEffect(() => {
  //   // TODO: this is not working, navigating to a single observation doesn't reset the loading indicator
  //   console.log("pending " + pending());
  //   loadingIndicator(pending());
  // });

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
        <div class="-my-2 sm:-mx-6 lg:-mx-8">
          <div class="py-2 align-middle inline-block w-full sm:px-6 lg:px-8">
            <div class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
              <Show
                when={observations()}
                fallback={
                  <div class="w-full py-12 bg-white">
                    <div class="text-center">
                      <CubeOutline class="mx-auto h-12 w-12 text-gray-400" />
                      <h3 class="mt-2 text-sm font-medium text-gray-900">
                        No observations found
                      </h3>
                      <p class="mt-1 text-sm text-gray-500">
                        Try adjusting your search or filter to find what you're
                        looking for.
                      </p>
                    </div>
                  </div>
                }
              >
                <ObservationsTable observations={observations() ?? []} />

                <Pagination
                  pageMeta={data()!.page}
                  onPageChange={(page) => {
                    setSearchParams(
                      { page: page.toString() },
                      { scroll: true }
                    );
                  }}
                />
              </Show>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Observations;
