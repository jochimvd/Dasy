import { ParentComponent } from "solid-js";

const ConfigurationDetailLayout: ParentComponent<{
  heading: string;
  subheading?: string;
  actions: {
    edit?: () => void;
    delete?: () => void;
  };
}> = (props) => {
  return (
    <section aria-labelledby="configuration-heading">
      <div class="shadow sm:rounded-md sm:overflow-hidden">
        <div class="bg-white py-6 px-4 sm:p-6">
          <div class="flex items-center">
            <div>
              <h2
                id="configuration-heading"
                class="text-lg leading-6 font-medium text-gray-900"
              >
                {props.heading}
              </h2>
              {props.subheading && (
                <p class="mt-1 text-sm text-gray-500">{props.subheading}</p>
              )}
            </div>
            <div class="ml-auto">
              {props.actions.edit && (
                <button
                  class="inline-flex items-center px-2.5 py-1.5 border border-orange-600 shadow-sm text-xs font-medium rounded text-orange-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                  onClick={props.actions.edit}
                >
                  Edit
                </button>
              )}
              {props.actions.delete && (
                <button
                  class="ml-2 inline-flex items-center px-2.5 py-1.5 border border-red-600 shadow-sm text-xs font-medium rounded text-red-600 bg-white hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                  onClick={props.actions.delete}
                >
                  Delete
                </button>
              )}
            </div>
          </div>
          <div class="mt-6">{props.children}</div>
        </div>
      </div>
    </section>
  );
};

export default ConfigurationDetailLayout;
