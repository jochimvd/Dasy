import { ChartConfiguration } from "chart.js";
import { Component, createEffect, createResource } from "solid-js";
import { createStore } from "solid-js/store";
import ReportService from "../../services/ReportService";
import ChartWrapper from "./ChartWrapper";

const TopPrioritiesPerCategory: Component<{ month: number }> = (props) => {
  const reportService = ReportService();

  const [categoryReport] = createResource(() => {
    return { year: 2022, month: props.month };
  }, reportService.categoryReport);

  const categoryData = () =>
    categoryReport()
      ?._embedded?.categories.filter((category) => category.score > 0)
      .sort((a, b) => b.score - a.score)
      .slice(0, 3);

  const lowHangingFruit = () =>
    categoryReport()
      ?._embedded?.categories.filter((category) => category.score > 0)
      .sort((a, b) => b.score - a.score)
      .reverse()
      .slice(0, 3);

  const config: ChartConfiguration = {
    type: "pie",
    options: {
      plugins: {
        legend: {
          position: "right",
        },
      },
    },
    data: {
      labels: [],
      datasets: [],
    },
  };

  const [dataStore, setDataStore] = createStore({ version: 1, config: config });

  createEffect(() => {
    const data = categoryData();

    if (!data) return;
    setDataStore(
      "config",
      "data",
      "labels",
      data.map((report) => report.category.name)
    );
    setDataStore("config", "data", "datasets", [
      {
        data: data.map((report) => report.score),
        label: "Score",
      },
    ]);
    setDataStore("version", (version) => version + 1);
  });

  return (
    <div class="relative mt-6 p-4 w-full md:w-1/2 h-[400px]">
      <h2 class="text-xl">Top priorities</h2>
      <div class="relative w-full h-[300px]">
        <ChartWrapper {...dataStore} />
      </div>
      {lowHangingFruit() && lowHangingFruit()!.length > 0 && (
        <p class="text-sm">
          Low-hanging fruit:{" "}
          {lowHangingFruit()
            ?.map((e) => e.category.name)
            .join(", ")}
        </p>
      )}
    </div>
  );
};

export default TopPrioritiesPerCategory;
