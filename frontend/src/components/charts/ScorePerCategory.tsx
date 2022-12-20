import { ChartConfiguration } from "chart.js";
import { Component, createEffect, createResource } from "solid-js";
import { createStore } from "solid-js/store";
import ReportService from "../../services/ReportService";
import ChartWrapper from "./ChartWrapper";

const ScorePerCategory: Component<{ month: number }> = (props) => {
  const reportService = ReportService();

  const [categoryReport] = createResource(() => {
    return { year: 2022, month: props.month };
  }, reportService.categoryReport);

  const categoryData = () => {
    let report = categoryReport()?._embedded?.categories;

    const sum = report?.reduce((acc, category) => {
      return acc + category.score;
    }, 0);

    report?.map((category) => {
      const percentage = (category.score / sum) * 100;
      category.score = Math.round(percentage * 1000) / 1000;
    });

    return report
      ?.filter((category) => category.score > 0)
      .sort((a, b) => b.score - a.score);
  };

  const config: ChartConfiguration = {
    type: "pie",
    options: {
      plugins: {
        legend: {
          position: "right",
          labels: {
            filter: (item) => (item.index > 5 ? false : true),
          },
        },
        tooltip: {
          callbacks: {
            label: (item) => `${item.dataset.label}: ${item.formattedValue}%`,
          },
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
      <h2 class="text-xl">Score per category</h2>
      <ChartWrapper {...dataStore} />
    </div>
  );
};

export default ScorePerCategory;
