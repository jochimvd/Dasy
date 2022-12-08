import { Navigate, Route, Routes } from "@solidjs/router";
import { Component, lazy, Suspense } from "solid-js";
import Loader from "./components/Loader";
import { ProtectedRoute } from "./components/ProtectedRoute";
import ErrorBoundaries from "./pages/errors/ErrorBoundaries";
import CategoryService from "./services/CategoryService";
import ConsequenceService from "./services/ConsequenceService";
import LocationService from "./services/LocationService";
import ObservationService from "./services/ObservationService";
import SeverityService from "./services/SeverityService";

const NavbarLayout = lazy(() => import("./layouts/NavbarLayout"));
const Home = lazy(() => import("./pages/home/Home"));
const Observations = lazy(() => import("./pages/observations/Observations"));
const Observation = lazy(() => import("./pages/observations/Observation"));
const ObservationEdit = lazy(
  () => import("./pages/observations/ObservationEdit")
);
const Severities = lazy(
  () => import("./pages/configuration/severities/Severities")
);
const Severity = lazy(
  () => import("./pages/configuration/severities/Severity")
);
const SeverityEdit = lazy(
  () => import("./pages/configuration/severities/SeverityEdit")
);
const Categories = lazy(
  () => import("./pages/configuration/categories/Categories")
);
const Category = lazy(
  () => import("./pages/configuration/categories/Category")
);
const CategoryEdit = lazy(
  () => import("./pages/configuration/categories/CategoryEdit")
);
const Consequences = lazy(
  () => import("./pages/configuration/consequences/Consequences")
);
const Consequence = lazy(
  () => import("./pages/configuration/consequences/Consequence")
);
const ConsequenceEdit = lazy(
  () => import("./pages/configuration/consequences/ConsequenceEdit")
);
const Locations = lazy(
  () => import("./pages/configuration/locations/Locations")
);
const Location = lazy(() => import("./pages/configuration/locations/Location"));
const LocationEdit = lazy(
  () => import("./pages/configuration/locations/LocationEdit")
);

const ConfigurationLayout = lazy(() => import("./layouts/ConfigurationLayout"));
const General = lazy(() => import("./pages/configuration/General"));
const Board = lazy(() => import("./pages/board/Board"));
const Login = lazy(() => import("./pages/users/Login"));
const NotFoundPage = lazy(() => import("./pages/errors/NotFoundPage"));

const App: Component = () => {
  const observationService = ObservationService();
  const locationService = LocationService();
  const severityService = SeverityService();
  const consequenceService = ConsequenceService();
  const categoryService = CategoryService();

  return (
    <>
      <Loader />
      <ErrorBoundaries>
        <Suspense>
          <Routes>
            <ProtectedRoute redirect="/login">
              <Route path="/" component={NavbarLayout}>
                <Route path="/" element={<Navigate href={"/observations"} />} />
                <Route path="/board" component={Board} />
                <Route path="/observations">
                  <Route
                    path="/"
                    component={Observations}
                    data={observationService.observationsData}
                  />
                  <Route path="/:id" data={observationService.observationData}>
                    <Route path="/" component={Observation} />
                    <Route path="/edit" component={ObservationEdit} />
                  </Route>
                  <Route
                    path="/create"
                    component={ObservationEdit}
                    data={observationService.observationData}
                  />
                </Route>
                <Route path="/configuration" component={ConfigurationLayout}>
                  <Route path="/" component={General} />
                  <Route path="/categories">
                    <Route
                      path="/"
                      component={Categories}
                      data={categoryService.categoriesData}
                    />
                    <Route path="/:id" data={categoryService.categoryData}>
                      <Route path="/" component={Category} />
                      <Route path="/edit" component={CategoryEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={CategoryEdit}
                      data={categoryService.categoryData}
                    />
                  </Route>
                  <Route path="/consequences">
                    <Route
                      path="/"
                      component={Consequences}
                      data={consequenceService.consequencesData}
                    />
                    <Route
                      path="/:id"
                      data={consequenceService.consequenceData}
                    >
                      <Route path="/" component={Consequence} />
                      <Route path="/edit" component={ConsequenceEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={ConsequenceEdit}
                      data={consequenceService.consequenceData}
                    />
                  </Route>
                  <Route path="/severities">
                    <Route
                      path="/"
                      component={Severities}
                      data={severityService.severitiesData}
                    />
                    <Route path="/:id" data={severityService.severityData}>
                      <Route path="/" component={Severity} />
                      <Route path="/edit" component={SeverityEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={SeverityEdit}
                      data={severityService.severityData}
                    />
                  </Route>
                  <Route path="/locations">
                    <Route
                      path="/"
                      component={Locations}
                      data={locationService.locationsData}
                    />
                    <Route path="/:id" data={locationService.locationData}>
                      <Route path="/" component={Location} />
                      <Route path="/edit" component={LocationEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={LocationEdit}
                      data={locationService.locationData}
                    />
                  </Route>
                </Route>
              </Route>
            </ProtectedRoute>

            <Route path="/login" component={Login} />
            <Route path={["*any"]} component={NotFoundPage} />
          </Routes>
        </Suspense>
      </ErrorBoundaries>
    </>
  );
};

export default App;
