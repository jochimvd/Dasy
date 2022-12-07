export type Links = {
  self: {
    href: string;
  };
  put?: {
    href: string;
  };
  patch?: {
    href: string;
  };
  delete?: {
    href: string;
  };
  editableFields?: {
    href: string;
  };
  [key: string]:
    | {
        href: string;
      }
    | undefined;
};

export const parseJwt = (token: string) => {
  let base64Url = token.split(".")[1];
  let base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  let jsonPayload = decodeURIComponent(
    window
      .atob(base64)
      .split("")
      .map(function (c) {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join("")
  );

  return JSON.parse(jsonPayload);
};

export const formatDate = (date: Date) =>
  date.toLocaleString([], {
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
