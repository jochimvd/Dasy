import { Accessor, ParentComponent } from "solid-js";

const ConfigurationEditLayout: ParentComponent<{
  heading: string;
  subheading?: string;
  saving: Accessor<boolean>;
  onSubmit: (e: Event) => void;
}> = (props) => {
  return (
    <section aria-labelledby="configuration-heading">
      <form>
        <div class="shadow sm:rounded-md sm:overflow-hidden">
          <div class="bg-white py-6 px-4 sm:p-6">
            <div>
              <h2
                id="configuration-heading"
                class="text-lg leading-6 font-medium text-gray-900"
              >
                {props.heading}
              </h2>
              <p class="mt-1 text-sm text-gray-500">{props.subheading}</p>
            </div>
            {props.children}
          </div>
          <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
            <button
              type="submit"
              class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
              onClick={props.onSubmit}
            >
              {props.saving() ? "Saving..." : "Save"}
            </button>
          </div>
        </div>
      </form>
    </section>
  );
};

export default ConfigurationEditLayout;
