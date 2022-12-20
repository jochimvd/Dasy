import { Component, createSignal, useTransition } from "solid-js";
import CategoryEvolution from "../../components/charts/CategoryEvolution";
import CategoryEvolutions from "../../components/charts/CategoryEvolutions";
import FollowUpCategories from "../../components/charts/FollowUpCategories";
import ObservationsPerSite from "../../components/charts/ObservationsPerSite";
import ScorePerCategory from "../../components/charts/ScorePerCategory";
import TopPrioritiesPerCategory from "../../components/charts/TopPrioritiesPerCategory";
import SelectMonth from "../../components/SelectMonth";

const Reports: Component = () => {
  const [, start] = useTransition();
  const [month, setMonth] = createSignal(10);

  return (
    <div class="bg-white overflow-hidden shadow sm:rounded-lg">
      <div class="px-4 py-5 sm:p-6">
        <div class="sm:w-1/3">
          <SelectMonth
            value={month()}
            setMonth={(id) => start(() => setMonth(id))}
          />
        </div>

        <div class="flex flex-wrap">
          <ScorePerCategory month={month()} />

          <TopPrioritiesPerCategory month={month()} />

          <CategoryEvolutions month={month()} />

          <FollowUpCategories month={month()} />

          <ObservationsPerSite month={month()} />

          <CategoryEvolution />
        </div>
      </div>
    </div>
  );
};

export default Reports;
