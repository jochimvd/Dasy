import ky from "ky";

const apiBaseUrl = import.meta.env.VITE_API_URL;

export const api = ky.create({prefixUrl: apiBaseUrl});

export const getUri = (apiUrl?: string) => {
  if (!apiUrl) {
    console.error("apiUrl is not defined");
    return apiBaseUrl
  }

  return apiUrl.replace(apiBaseUrl, "")
}

export type Links = {
  self: {
    href: string;
  },
  edit?: {
    href: string;
  }
  delete?: {
    href: string;
  }
}
