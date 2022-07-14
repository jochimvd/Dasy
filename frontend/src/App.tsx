import { Outlet, Route, Routes } from "solid-app-router";
import type { Component } from "solid-js";
import NavBar from "./components/NavBar";
import Home from "./pages/Home/Home";
import Observations from "./pages/observations/Observations";
import Observation from "./pages/observations/Observation";
import Severities from "./pages/settings/severities/Severities";
import Severity from "./pages/settings/severities/Severity";
import SeverityEdit from "./pages/settings/severities/SeverityEdit";
import Categories from "./pages/settings/categories/Categories";
import Category from "./pages/settings/categories/Category";
import CategoryEdit from "./pages/settings/categories/CategoryEdit";
import Consequences from "./pages/settings/consequences/Consequences";
import Consequence from "./pages/settings/consequences/Consequence";
import Locations from "./pages/settings/locations/Locations";
import LocationEdit from "./pages/settings/locations/LocationEdit";
import Location from "./pages/settings/locations/Location";
import ConsequenceEdit from "./pages/settings/consequences/ConsequenceEdit";
import SettingsWrapper from "./pages/settings/SettingsWrapper";
import Settings from "./pages/settings/Settings";
import Board from "./pages/board/Board";

const App: Component = () => {
  return (
    <>
      <NavBar />

      <div class="max-w-7xl mt-4 mx-auto">
        <div class="mx-4">
          <Outlet />
          <Routes>
            <Route path="/" component={Home} />
            <Route path="/board">
              <Route path="/" component={Board} />
            </Route>
            <Route path="/observations">
              <Route path="/" component={Observations} />
              <Route path="/:id" component={Observation} />
            </Route>
            <Route path="/settings" component={SettingsWrapper}>
              <Route path="/" component={Settings} />
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
        </div>
      </div>
    </>
  );
};

export default App;
