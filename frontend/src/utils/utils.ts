import ky from "ky";

const apiBaseUrl = import.meta.env.VITE_API_URL;

export const api = ky.create({
  prefixUrl: apiBaseUrl,
  hooks: {
    beforeError: [
      error => {
        const {response} = error;

        if (response.status === 404) {
          return error;
        }

        return error;
      }
    ]
  }});

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
  put?: {
    href: string;
  },
  patch?: {
    href: string;
  }
  delete?: {
    href: string;
  }
  editableFields?: {
    href: string;
  },
  [key: string]: {
    href: string
  } | undefined
}
