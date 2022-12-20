import { Navigate, Route, Routes } from "@solidjs/router";
import { Component, lazy, Suspense } from "solid-js";
import Loader from "./components/Loader";
import { ProtectedRoute } from "./components/ProtectedRoute";
import ErrorBoundaries from "./pages/errors/ErrorBoundaries";
import CategoryService from "./services/CategoryService";
import ReoccurrenceService from "./services/ReoccurrenceService";
import SiteService from "./services/SiteService";
import ObservationService from "./services/ObservationService";
import SeverityService from "./services/SeverityService";
import TypeService from "./services/TypeService";

const NavbarLayout = lazy(() => import("./layouts/NavbarLayout"));
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
const Reoccurrences = lazy(
  () => import("./pages/configuration/reoccurrences/Reoccurrences")
);
const Reoccurrence = lazy(
  () => import("./pages/configuration/reoccurrences/Reoccurrence")
);
const ReoccurrenceEdit = lazy(
  () => import("./pages/configuration/reoccurrences/ReoccurrenceEdit")
);
const Sites = lazy(() => import("./pages/configuration/sites/Sites"));
const Site = lazy(() => import("./pages/configuration/sites/Site"));
const SiteEdit = lazy(() => import("./pages/configuration/sites/SiteEdit"));
const Types = lazy(() => import("./pages/configuration/types/Types"));
const Type = lazy(() => import("./pages/configuration/types/Type"));
const TypeEdit = lazy(() => import("./pages/configuration/types/TypeEdit"));

const ConfigurationLayout = lazy(() => import("./layouts/ConfigurationLayout"));
const General = lazy(() => import("./pages/configuration/General"));
const Board = lazy(() => import("./pages/board/Board"));
const Reports = lazy(() => import("./pages/reports/Reports"));
const Login = lazy(() => import("./pages/users/Login"));
const ResetPassword = lazy(() => import("./pages/users/ResetPassword"));
const NotFoundPage = lazy(() => import("./pages/errors/NotFoundPage"));

const App: Component = () => {
  const observationService = ObservationService();
  const siteService = SiteService();
  const severityService = SeverityService();
  const reoccurrenceService = ReoccurrenceService();
  const categoryService = CategoryService();
  const typeService = TypeService();

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
                <Route path="/reports" component={Reports} />
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
                  <Route path="/reoccurrences">
                    <Route
                      path="/"
                      component={Reoccurrences}
                      data={reoccurrenceService.reoccurrencesData}
                    />
                    <Route
                      path="/:id"
                      data={reoccurrenceService.reoccurrenceData}
                    >
                      <Route path="/" component={Reoccurrence} />
                      <Route path="/edit" component={ReoccurrenceEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={ReoccurrenceEdit}
                      data={reoccurrenceService.reoccurrenceData}
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
                  <Route path="/sites">
                    <Route
                      path="/"
                      component={Sites}
                      data={siteService.sitesData}
                    />
                    <Route path="/:id" data={siteService.siteData}>
                      <Route path="/" component={Site} />
                      <Route path="/edit" component={SiteEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={SiteEdit}
                      data={siteService.siteData}
                    />
                  </Route>
                  <Route path="/types">
                    <Route
                      path="/"
                      component={Types}
                      data={typeService.typesData}
                    />
                    <Route path="/:id" data={typeService.typeData}>
                      <Route path="/" component={Type} />
                      <Route path="/edit" component={TypeEdit} />
                    </Route>
                    <Route
                      path="/create"
                      component={TypeEdit}
                      data={typeService.typeData}
                    />
                  </Route>
                </Route>
              </Route>
            </ProtectedRoute>

            <Route path="/login" component={Login} />
            <Route path="/login/reset-password" component={ResetPassword} />
            <Route path={["*any"]} component={NotFoundPage} />
          </Routes>
        </Suspense>
      </ErrorBoundaries>
    </>
  );
};

export default App;
