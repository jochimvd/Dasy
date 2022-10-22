import { Outlet, Route, Routes, useNavigate } from "solid-app-router";
import { Component, ErrorBoundary } from "solid-js";
import NavBar from "./components/NavBar";
import Home from "./pages/home/Home";
import Observations from "./pages/observations/Observations";
import Observation from "./pages/observations/Observation";
import Severities from "./pages/configuration/severities/Severities";
import Severity from "./pages/configuration/severities/Severity";
import SeverityEdit from "./pages/configuration/severities/SeverityEdit";
import Categories from "./pages/configuration/categories/Categories";
import Category from "./pages/configuration/categories/Category";
import CategoryEdit from "./pages/configuration/categories/CategoryEdit";
import Consequences from "./pages/configuration/consequences/Consequences";
import Consequence from "./pages/configuration/consequences/Consequence";
import Locations from "./pages/configuration/locations/Locations";
import LocationEdit from "./pages/configuration/locations/LocationEdit";
import Location from "./pages/configuration/locations/Location";
import ConsequenceEdit from "./pages/configuration/consequences/ConsequenceEdit";
import ConfigurationWrapper from "./pages/configuration/ConfigurationWrapper";
import General from "./pages/configuration/General";
import Board from "./pages/board/Board";
import ObservationEdit from "./pages/observations/ObservationEdit";
import ErrorBoundaries from "./pages/errors/ErrorBoundaries";

const App: Component = () => {
  return (
    <>
      <NavBar />

      <ErrorBoundaries>
        <main class="max-w-7xl mx-auto pt-6 sm:pt-9 sm:px-6 lg:pt-12 lg:px-8">
          <Outlet />
          <Routes>
            <Route path="/" component={Home} />
            <Route path="/board">
              <Route path="/" component={Board} />
            </Route>
            <Route path="/observations">
              <Route path="/" component={Observations} />
              <Route path="/:id" component={Observation} />
              <Route path="/:id/edit" component={ObservationEdit} />
              <Route path="/create" element={<ObservationEdit new={true} />} />
            </Route>
            <Route path="/configuration" component={ConfigurationWrapper}>
              <Route path="/" component={General} />
              <Route path="/severities">
                <Route path="/" component={Severities} />
                <Route path="/:id" component={Severity} />
                <Route path="/:id/edit" component={SeverityEdit} />
                <Route path="/create" element={<SeverityEdit new={true} />} />
              </Route>
              <Route path="/categories">
                <Route path="/" component={Categories} />
                <Route path="/:id" component={Category} />
                <Route path="/:id/edit" component={CategoryEdit} />
                <Route path="/create" element={<CategoryEdit new={true} />} />
              </Route>
              <Route path="/consequences">
                <Route path="/" component={Consequences} />
                <Route path="/:id" component={Consequence} />
                <Route path="/:id/edit" component={ConsequenceEdit} />
                <Route
                  path="/create"
                  element={<ConsequenceEdit new={true} />}
                />
              </Route>
              <Route path="/locations">
                <Route path="/" component={Locations} />
                <Route path="/:id" component={Location} />
                <Route path="/:id/edit" component={LocationEdit} />
                <Route path="/create" element={<LocationEdit new={true} />} />
              </Route>
            </Route>
          </Routes>
        </main>
      </ErrorBoundaries>
    </>
  );
};

export default App;
