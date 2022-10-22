export const enum Status {
  New = "NEW",
  InProgress = "IN_PROGRESS",
  Done = "DONE",
}

export const Statuses = [Status.New, Status.InProgress, Status.Done];

export const formatStatus = (status: Status) => {
  return status.slice(0, 1) + status.slice(1).toLowerCase().replace("_", " ");
};

export const prettyFormatStatus = (
  status: Status | undefined,
  size: "small" | "large" = "small"
) => {
  if (!status) {
    return "";
  }

  return (
    <span
      class="whitespace-nowrap inline-flex items-center font-medium"
      classList={{
        "bg-green-100 text-green-800": status === Status.Done,
        "bg-blue-100 text-blue-800": status === Status.InProgress,
        "bg-gray-100 text-gray-800": status === Status.New,
        "px-2 py-0.5 rounded text-xs": size === "small",
        "px-2.5 py-0.5 rounded-md text-sm": size === "large",
      }}
    >
      {formatStatus(status)}
    </span>
  );
};
