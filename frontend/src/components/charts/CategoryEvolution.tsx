import { ChartConfiguration } from "chart.js";
import {
  Component,
  createEffect,
  createResource,
  createSignal,
} from "solid-js";
import { createStore } from "solid-js/store";
import CategoryService from "../../services/CategoryService";
import ReportService from "../../services/ReportService";
import SelectCategory from "../SelectCategory";
import ChartWrapper from "./ChartWrapper";

const CategoryEvolution: Component = () => {
  const reportService = ReportService();
  const categoryService = CategoryService();

  const [categories] = categoryService.categoriesData();

  const year = 2022;

  // TODO: create api endpoint for this
  const [januaryReport] = createResource(() => {
    return { year: year, month: 1 };
  }, reportService.categoryReport);
  const januaryData = () => januaryReport()?._embedded?.categories;
  const [februaryReport] = createResource(() => {
    return { year: year, month: 2 };
  }, reportService.categoryReport);
  const februaryData = () => februaryReport()?._embedded?.categories;
  const [marchReport] = createResource(() => {
    return { year: year, month: 3 };
  }, reportService.categoryReport);
  const marchData = () => marchReport()?._embedded?.categories;
  const [aprilReport] = createResource(() => {
    return { year: year, month: 4 };
  }, reportService.categoryReport);
  const aprilData = () => aprilReport()?._embedded?.categories;
  const [mayReport] = createResource(() => {
    return { year: year, month: 5 };
  }, reportService.categoryReport);
  const mayData = () => mayReport()?._embedded?.categories;
  const [juneReport] = createResource(() => {
    return { year: year, month: 6 };
  }, reportService.categoryReport);
  const juneData = () => juneReport()?._embedded?.categories;
  const [julyReport] = createResource(() => {
    return { year: year, month: 7 };
  }, reportService.categoryReport);
  const julyData = () => julyReport()?._embedded?.categories;
  const [augustReport] = createResource(() => {
    return { year: year, month: 8 };
  }, reportService.categoryReport);
  const augustData = () => augustReport()?._embedded?.categories;
  const [septemberReport] = createResource(() => {
    return { year: year, month: 9 };
  }, reportService.categoryReport);
  const septemberData = () => septemberReport()?._embedded?.categories;
  const [octoberReport] = createResource(() => {
    return { year: year, month: 10 };
  }, reportService.categoryReport);
  const octoberData = () => octoberReport()?._embedded?.categories;
  const [novemberReport] = createResource(() => {
    return { year: year, month: 11 };
  }, reportService.categoryReport);
  const novemberData = () => novemberReport()?._embedded?.categories;
  const [decemberReport] = createResource(() => {
    return { year: year, month: 12 };
  }, reportService.categoryReport);
  const decemberData = () => decemberReport()?._embedded?.categories;

  const monthData = [
    januaryData,
    februaryData,
    marchData,
    aprilData,
    mayData,
    juneData,
    julyData,
    augustData,
    septemberData,
    octoberData,
    novemberData,
    decemberData,
  ];

  const [cat, setCat] = createSignal(1);

  const getData = (cat: number) => {
    const data: number[] = [];
    monthData.map((categories) => {
      categories()?.map((category) => {
        if (category.category.id === cat) {
          data.push(category.score);
        }
      });
    });
    return data;
  };

  const config: ChartConfiguration = {
    type: "line",
    options: {
      plugins: {
        legend: {
          position: "bottom",
        },
      },
    },
    data: {
      labels: [
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
      ],
      datasets: [
        {
          label: "Score",
          data: [],
        },
      ],
    },
  };

  const [dataStore, setDataStore] = createStore({ version: 1, config: config });

  createEffect(() => {
    // get all months data
    const data = categories()?._embedded?.categories.map((c) => {
      return {
        label: c.name,
        data: getData(c.id),
        hidden: c.id !== cat(),
      };
    });

    if (!data) return;
    setDataStore("config", "data", "datasets", data);
    setDataStore("version", (version) => version + 1);
  });

  return (
    <div class="w-full p-6">
      <SelectCategory
        wrapperClass="sm:w-1/3"
        value={cat()}
        setCategory={(id) => setCat(id)}
      />

      <div class="relative mt-6 w-full h-[400px]">
        <h2 class="text-xl">Category evolution over time</h2>
        <ChartWrapper {...dataStore} />
      </div>
    </div>
  );
};

export default CategoryEvolution;
