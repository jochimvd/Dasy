import Chart, { ChartConfiguration } from "chart.js/auto";
import { Component, createEffect, onMount } from "solid-js";
import { unwrap } from "solid-js/store";
import { emptyChartPlugin } from "./plugins";

export type ChartProps = {
  version: number;
  config: ChartConfiguration;
};

const ChartWrapper: Component<ChartProps> = (props) => {
  let chart: HTMLCanvasElement | undefined;
  let chartInstance: Chart | undefined;

  onMount(() => {
    if (!chart) {
      console.error("No canvas element found");
      return;
    }
    chartInstance = new Chart(chart, {
      ...unwrap(props.config),
      plugins: [emptyChartPlugin(() => props.version)],
    });
  });

  createEffect(() => {
    if (chartInstance === undefined) return;
    props.version && chartInstance.update();
  });

  return <canvas ref={chart}></canvas>;
};

export default ChartWrapper;
