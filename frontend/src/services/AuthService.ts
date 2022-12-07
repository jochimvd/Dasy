import ky, { Options } from "ky";
import { batch, createEffect, createResource, createSignal } from "solid-js";
import { createStore } from "solid-js/store";
import { UserDto } from "../models/User";
import { parseJwt } from "../utils/utils";

const API_ROOT = import.meta.env.VITE_API_URL;

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
};

export type LoginInput = {
  email: string;
  password: string;
  rememberMe?: boolean;
};

export type Auth = {
  isLoggedIn: boolean;
  loggedInId?: string;
  user?: UserDto;
  token?: string;
  refreshToken?: string;
};

const createSessionStore = <T extends object>(key: string, initialValue: T) => {
  const persisted = sessionStorage.getItem(key);
  const store = createStore<T>(
    persisted ? (JSON.parse(persisted) as T) : initialValue
  );
  createEffect(() => {
    sessionStorage.setItem(key, JSON.stringify(store[0]));
  });
  return store;
};

const AuthService = () => {
  const [session, setSession] = createSessionStore<Auth>("auth", {
    isLoggedIn: false,
  });

  let refreshRequest: Promise<LoginResponse> | null;
  const refreshTokens = () => {
    // Prevent concurrent refresh request initialization
    if (!refreshRequest) {
      refreshRequest = ky(`${API_ROOT}/users/refresh`, {
        method: "POST",
        json: { refreshToken: session.refreshToken },
      })
        .json<LoginResponse>()
        .then((tokens) => {
          // Update session
          doLogin(tokens);

          return tokens;
        })
        .finally(() => {
          // Reset refresh request promise
          refreshRequest = null;
        });
    }

    return refreshRequest;
  };

  const doLogin = (loginResponse: LoginResponse) => {
    const userId = parseJwt(loginResponse.accessToken).sub as string;
    setSession({
      isLoggedIn: true,
      loggedInId: userId,
      token: loginResponse.accessToken,
      refreshToken: loginResponse.refreshToken,
    });
  };

  const api = ky.create({
    prefixUrl: API_ROOT,
    hooks: {
      beforeRequest: [
        async (request) => {
          const token = session.token;
          if (session.isLoggedIn && token) {
            request.headers.set("Authorization", `Bearer ${token}`);
          }
        },
      ],
      afterResponse: [
        async (request, options, response) => {
          if (response.status === 401) {
            if ((await response.json()).message === "jwt token expired") {
              try {
                // Get a fresh token
                const tokens = await refreshTokens();

                // Retry with the token
                request.headers.set(
                  "Authorization",
                  `Bearer ${tokens.accessToken}`
                );

                return ky(request);
              } catch (err: any) {
                // If we can't get a fresh token, sign out
                service.logout();
              }
            } else {
              service.logout();
            }
          }
        },
      ],
    },
  });

  const send = async <T>(method: string, url: string, data?: any) => {
    const options: Options = { method };

    if (data !== undefined) {
      options.json = data;
    }

    try {
      return await api(url, options).json<T>();
    } catch (err: any) {
      console.error("request failed: ", err);
      throw err;
    }
  };

  const [currentUser, { mutate }] = createResource(
    () => session.loggedInId,
    async (id) => await send<UserDto>("GET", `users/${id}`)
  );

  createEffect(() => {
    console.log("currentUser", currentUser());
    setSession("user", currentUser());
  });

  const Auth = {
    login: (loginInput: LoginInput) =>
      send<LoginResponse>("POST", "users/login", loginInput),
    logout: () => {
      throw new Error("Not implemented");
    },
  };

  const service = {
    get state() {
      return session;
    },

    send,

    async login(loginInput: LoginInput) {
      const loginResponse = await Auth.login(loginInput);
      doLogin(loginResponse);
    },
    logout() {
      batch(() => {
        mutate(undefined);
        setSession({
          isLoggedIn: false,
          loggedInId: undefined,
          token: undefined,
          refreshToken: undefined,
          user: undefined,
        });
      });
    },
  };

  return service;
};

export default AuthService;
