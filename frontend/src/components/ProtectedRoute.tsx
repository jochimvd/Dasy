import { Route } from "@solidjs/router";
import { useLocation, Outlet, Navigate } from "@solidjs/router";
import { createMemo, JSXElement, on, Show } from "solid-js";
import { useService } from "solid-services";
import AuthService from "../services/AuthService";

type ProtectedRouteProps = {
  path?: string;
  redirect: string;
  children: JSXElement;
};

export const ProtectedRoute = (props: ProtectedRouteProps) => {
  return (
    <Route
      path={props.path || "/"}
      element={<Protected redirect={props.redirect} />}
    >
      {props.children}
    </Route>
  );
};

type ProtectedProps = {
  redirect: string;
};

const Protected = (props: ProtectedProps) => {
  const location = useLocation();
  const auth = useService(AuthService);

  // This is here to determine the returnTo query parameter when accessing a protected page
  const loginPath = createMemo(
    on(
      () => auth().state.isLoggedIn,
      (_, wasLoggedIn) => {
        if (!wasLoggedIn && location.pathname !== "/") {
          return `${props.redirect}?returnTo=${encodeURI(location.pathname)}`;
        }
        return props.redirect;
      }
    )
  );

  // Show the outlet when logged in, otherwise redriect to the login page
  return (
    <Show
      when={auth().state.isLoggedIn}
      fallback={<Navigate href={loginPath()} />}
    >
      <Outlet />
    </Show>
  );
};
