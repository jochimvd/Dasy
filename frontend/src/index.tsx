/* @refresh reload */
import { render } from "solid-js/web";

import "./index.css";
import App from "./App";
import { Router } from "@solidjs/router";
import { ServiceRegistry } from "solid-services";

render(
  () => (
    <Router>
      <ServiceRegistry>
        <App />
      </ServiceRegistry>
    </Router>
  ),
  document.getElementById("root") as HTMLElement
);
