import SolidChart, { SolidChartProps } from "solid-chart.js";
import {
  Component,
  createEffect,
  createResource,
  createSignal,
} from "solid-js";
import { api, Links } from "../../utils/utils";

type Category = {
  id?: number;
  name: string;
  priority: number;
  _links: Links;
};

type Response = {
  _embedded: {
    categories: Category[];
  };
};

const Home: Component = () => {
  const fetchAnalysis = async (month: number) => {
    const res = await api
      .post(`analyses/monthly`, { json: { month: month, year: 2022 } })
      .json<Response>();
    return res._embedded.categories;
  };

  const [chart, setChart] = createSignal({});
  const [pieChart, setPieChart] = createSignal({});

  const [month1, setMonth1] = createSignal(4);
  const [month2, setMonth2] = createSignal(5);

  const [analysis] = createResource(month1, fetchAnalysis);
  const [analysis2] = createResource(month2, fetchAnalysis);

  const colorFunction = (value: number, opacity: number = 1) => {
    if (value < 20) {
      return `rgba(75, 192, 192, ${opacity})`;
    }
    if (value < 70) {
      return `rgba(255, 205, 86, ${opacity})`;
    }
    if (value < 150) {
      return `rgba(255, 159, 64, ${opacity})`;
    }
    return `rgba(255, 99, 132, ${opacity})`;
  };

  createEffect(() => {
    const labels = analysis()?.map((c) => c.name);
    const prios1 = analysis()?.map((c) => c.priority);
    const data = {
      labels: labels,
      datasets: [
        {
          label: "Analysis 1",
          data: prios1,
          backgroundColor: prios1?.map((c) => colorFunction(c, 0.2)),
          borderColor: prios1?.map(colorFunction),
          borderWidth: 1,
        },
        {
          label: "Analysis 2",
          data: analysis2()?.map((c) => c.priority),
          backgroundColor: analysis2()
            ?.map((c) => c.priority)
            .map((c) => colorFunction(c, 0.2)),
          borderColor: analysis2()
            ?.map((c) => c.priority)
            .map(colorFunction),
          borderWidth: 1,
        },
      ],
    };

    const settings: SolidChartProps = {
      type: "bar",
      data: data,
    };

    setChart(settings);

    const cats = analysis()
      ?.filter((c) => c.priority > 20)
      .sort((a, b) => b.priority - a.priority);

    const pieLabels = cats?.map((c) => c.name);
    const piePrios1 = cats?.map((c) => c.priority);
    const pieData = {
      labels: pieLabels,
      datasets: [
        {
          label: "Analysis May",
          data: piePrios1,
          backgroundColor: piePrios1?.map((c) => colorFunction(c, 0.2)),
          borderColor: piePrios1?.map(colorFunction),
          borderWidth: 1,
        },
      ],
    };

    const pieSettings: SolidChartProps = {
      type: "pie",
      data: pieData,
      options: {
        legend: {
          display: true,
        },
      },
    };

    setPieChart(pieSettings);
  });

  const updateMonth1 = () => (event: Event) => {
    const inputElement = event.currentTarget as HTMLInputElement;
    setMonth1(+inputElement.value);
  };

  const updateMonth2 = () => (event: Event) => {
    const inputElement = event.currentTarget as HTMLInputElement;
    setMonth2(+inputElement.value);
  };

  return (
    <>
      <h1 class="text-xl">Home</h1>
      Month 1:{" "}
      <input
        type="number"
        placeholder="month1"
        value={month1()}
        onChange={updateMonth1()}
        min="1"
        max="12"
      />
      <br />
      Month 2:{" "}
      <input
        type="number"
        placeholder="month2"
        value={month2()}
        onChange={updateMonth2()}
        min="1"
        max="12"
      />
      <SolidChart
        {...chart}
        canvasOptions={{
          width: 1000,
          height: 400,
        }}
      />
      <div style="width: 500px; height: 500px">
        <SolidChart
          {...pieChart}
          canvasOptions={{
            width: 200,
            height: 200,
          }}
        />
      </div>
    </>
  );
};

export default Home;
