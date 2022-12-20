import { ChartConfiguration } from "chart.js";
import { Component, createEffect, createResource } from "solid-js";
import { createStore } from "solid-js/store";
import { preprocessCSS } from "vite";
import ReportService from "../../services/ReportService";
import ChartWrapper from "./ChartWrapper";

const CategoryEvolutions: Component<{ month: number }> = (props) => {
  const reportService = ReportService();

  const evolutionThreshold = 10;

  const [categoryReport] = createResource(() => {
    return { year: 2022, month: props.month };
  }, reportService.categoryReport);

  const [llastCategoryReport] = createResource(() => {
    return { year: 2022, month: ((props.month - 2) % 12) + 1 };
  }, reportService.categoryReport);

  const categoryData = () => {
    const currentCategoryReport = categoryReport()?._embedded?.categories;
    const lastCategoryReport = llastCategoryReport()?._embedded?.categories;

    if (props.month - 1 <= 4) return [];

    const evolution = currentCategoryReport
      ?.map((currentReport) => {
        const lastReport = lastCategoryReport?.find(
          (report) => report.category.id === currentReport.category.id
        );
        console.log(lastReport);
        const evolution = lastReport
          ? currentReport.score - lastReport.score
          : currentReport.score;
        return {
          name: currentReport.category.name,
          evolution: evolution,
        };
      })
      .filter((category) => Math.abs(category.evolution) > evolutionThreshold)
      .sort((a, b) => a.evolution - b.evolution);

    const positiveEvolutions = evolution?.filter(
      (category) => category.evolution < 0
    );
    const negativeEvolutions = evolution?.filter(
      (category) => category.evolution > 0
    );

    return [
      {
        data: positiveEvolutions,
        label: "Positive",
        // backgroundColor: "rgba(0, 255, 0, 0.5)",
      },
      {
        data: negativeEvolutions,
        label: "Negative",
        // backgroundColor: "rgba(255, 0, 0, 0.5)",
      },
    ];
  };

  const config: ChartConfiguration = {
    type: "bar",
    data: {
      labels: [],
      datasets: [],
    },
    options: {
      parsing: {
        xAxisKey: "name",
        yAxisKey: "evolution",
      },
      scales: {
        x: {
          stacked: true,
        },
      },
    },
  };

  const [dataStore, setDataStore] = createStore({ version: 1, config: config });

  createEffect(() => {
    const data = categoryData();

    if (!data) return;
    console.log("effect");
    setDataStore("config", "data", "datasets", data);
    setDataStore("version", (version) => version + 1);
  });

  return (
    <div class="relative mt-6 p-4 w-full md:w-1/2 max-h-[400px]">
      <h2 class="text-xl">Positive & negative evolution</h2>
      <ChartWrapper {...dataStore} />
    </div>
  );
};

export default CategoryEvolutions;
