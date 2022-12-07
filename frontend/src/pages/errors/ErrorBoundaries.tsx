import { HTTPError } from "ky";
import { Navigate } from "@solidjs/router";
import { ErrorBoundary, ParentComponent } from "solid-js";

export const ErrorBoundaries: ParentComponent = (props) => {
  return (
    <ErrorBoundary
      fallback={(err: Error, reset) => {
        if (err instanceof HTTPError) {
          const { response } = err;

          if (response.status === 401) {
            return <Navigate href="/login" />;
          }
          if (response.status === 403 || response.status === 404) {
            return <Navigate href="/not-found" />;
          }
        }

        return (
          <div>
            <h1>Something went wrong</h1>
            <p>{err.toString()}</p>
            <a href="/">Go back</a>
            {" | "}
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
