import { Component, mergeProps, Show } from "solid-js";
import { Page } from "../models/Common";
import { ChevronLeftS } from "../utils/Icons";

type PaginationProps = {
  pageMeta: Page;
  onPageChange: (page: number) => void;
  padding?: number;
};

const Pagination: Component<PaginationProps> = (props) => {
  props = mergeProps({ padding: 1 }, props);

  const nextPage = () => {
    setPage(props.pageMeta.number + 1);
  };

  const previousPage = () => {
    setPage(props.pageMeta.number - 1);
  };

  const setPage = (page: number) => {
    if (page < 0 || page > props.pageMeta.totalPages - 1) return;
    props.onPageChange(page);
  };

  const generatePageNumbers = (
    pageMeta: Page,
    step: number
  ): (string | number)[] => {
    let { number, totalPages } = pageMeta;

    if (2 * (step + 2) + 1 >= totalPages) {
      return [...Array(totalPages).keys()];
    }

    const sequence: any[] = [];
    totalPages = totalPages - 1;
    let startPage = number - step;
    let endPage = number + step;

    if (startPage <= 2) {
      endPage = Math.min(endPage - startPage + 2, totalPages);
      startPage = 0;
    }

    if (endPage + 2 >= totalPages) {
      startPage = Math.max(startPage - (endPage - totalPages + 2), 0);
      endPage = totalPages;
    }

    // first
    if (startPage >= 2) {
      sequence.push(0, "...");
    }

    // middle
    for (let page = startPage; page <= endPage; page++) {
      sequence.push(page);
    }

    // last
    if (endPage <= totalPages - 2) {
      sequence.push("...", totalPages);
    }

    return sequence;
  };

  return (
    <>
      <div class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
        <div class="flex-1 flex justify-between sm:hidden">
          <button
            onClick={() => previousPage()}
            disabled={props.pageMeta.number === 0}
            classList={{
              "opacity-50": props.pageMeta.number === 0,
            }}
            class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Previous
          </button>
          <button
            onClick={() => nextPage()}
            disabled={props.pageMeta.number === props.pageMeta.totalPages - 1}
            classList={{
              "opacity-50":
                props.pageMeta.number === props.pageMeta.totalPages - 1,
            }}
            class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Next
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <Show when={props.pageMeta.number < props.pageMeta.totalPages}>
              <p class="text-sm text-gray-700">
                Showing
                <span class="font-medium">
                  {" "}
                  {props.pageMeta.size * props.pageMeta.number + 1}{" "}
                </span>
                to
                <span class="font-medium">
                  {" "}
                  {Math.min(
                    props.pageMeta.size * (props.pageMeta.number + 1),
                    props.pageMeta.totalElements
                  )}{" "}
                </span>
                of
                <span class="font-medium">
                  {" "}
                  {props.pageMeta?.totalElements}{" "}
                </span>
                results
              </p>
            </Show>
          </div>
          <div>
            <nav
              class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px"
              aria-label="Pagination"
            >
              <button
                onClick={() => previousPage()}
                disabled={props.pageMeta.number === 0}
                classList={{
                  "opacity-50": props.pageMeta.number === 0,
                }}
                class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">Previous</span>
                <ChevronLeftS />
              </button>
              {generatePageNumbers(props.pageMeta, props.padding!).map(
                (page) => () => {
                  if (typeof page === "string") {
                    return (
                      <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                        ...
                      </span>
                    );
                  } else {
                    return (
                      <button
                        onClick={() => setPage(page)}
                        classList={{
                          "z-10 bg-orange-50 border-orange-500 text-orange-600":
                            props.pageMeta.number === page,
                          "bg-white border-gray-300 text-gray-500 hover:bg-gray-50":
                            props.pageMeta.number !== page,
                        }}
                        class="relative inline-flex items-center px-4 py-2 border text-sm font-medium"
                      >
                        {page + 1}
                      </button>
                    );
                  }
                }
              )}
              <button
                onClick={() => nextPage()}
                disabled={
                  props.pageMeta.number === props.pageMeta.totalPages - 1
                }
                classList={{
                  "opacity-50":
                    props.pageMeta.number === props.pageMeta.totalPages - 1,
                }}
                class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">Next</span>
                <ChevronLeftS class="h-5 w-5 rotate-180" />
              </button>
            </nav>
          </div>
        </div>
      </div>
    </>
  );
};

export default Pagination;
