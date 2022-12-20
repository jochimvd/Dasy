import { ChartConfiguration } from "chart.js";
import { Component, createEffect, createResource } from "solid-js";
import { createStore } from "solid-js/store";
import ReportService from "../../services/ReportService";
import ChartWrapper from "./ChartWrapper";

const ObservationsPerSite: Component<{ month: number }> = (props) => {
  const reportService = ReportService();

  const [siteReport] = createResource(() => {
    return { year: 2022, month: props.month };
  }, reportService.siteReport);
  const siteData = () => siteReport()?._embedded?.sites;

  const config: ChartConfiguration = {
    type: "bar",
    data: {
      labels: [],
      datasets: [],
    },
    options: {
      scales: {
        x: {
          stacked: true,
        },
        y: {
          stacked: true,
        },
      },
    },
  };

  const [dataStore, setDataStore] = createStore({ version: 1, config: config });

  createEffect(() => {
    const data = siteData();

    if (!data) return;
    setDataStore(
      "config",
      "data",
      "labels",
      data.map((report) => report.site.name)
    );
    setDataStore("config", "data", "datasets", [
      {
        data: data.map((report) => report.positiveObservations),
        label: "Positive",
      },
      {
        data: data.map((report) => report.observations),
        label: "Other",
      },
    ]);
    setDataStore("version", (version) => version + 1);
  });

  return (
    <div class="relative mt-6 p-4 w-full md:w-1/2 h-[400px]">
      <h2 class="text-xl">Observations per site</h2>
      <ChartWrapper {...dataStore} />
    </div>
  );
};

export default ObservationsPerSite;
