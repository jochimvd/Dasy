import { HTTPError } from "ky";
import { Navigate } from "solid-app-router";
import { Component, ErrorBoundary, JSXElement } from "solid-js";
import _404 from "./404";

type ErrorBoundariesProps = {
  children: JSXElement;
};

export const ErrorBoundaries: Component<ErrorBoundariesProps> = (props) => {
  return (
    <ErrorBoundary
      fallback={(err: Error, reset) => {
        if (err instanceof HTTPError) {
          const { response } = err;

          if (response.status === 401) {
            return <Navigate href="/login" />;
          }
          if (response.status === 404) {
            return <_404 />;
          }
        }

        return (
          <div>
            <h1>Something went wrong</h1>
            <p>{err.toString()}</p>
            <button onClick={reset}>Try again</button>
          </div>
        );
      }}
    >
      {props.children}
    </ErrorBoundary>
  );
};

export default ErrorBoundaries;
