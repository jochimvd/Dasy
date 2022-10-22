const General = () => {
  return (
    <>
      <section aria-labelledby="general-heading">
        <form>
          <div class="shadow sm:rounded-md sm:overflow-hidden">
            <div class="bg-white py-6 px-4 sm:p-6">
              <div>
                <h2
                  id="general-heading"
                  class="text-lg leading-6 font-medium text-gray-900"
                >
                  General Configuration
                </h2>
                <p class="mt-1 text-sm text-gray-500">
                  Update the servers' general configuration.
                </p>
              </div>

              <div class="mt-6 grid grid-cols-4 gap-6">
                <div class="col-span-4 sm:col-span-2">
                  <label
                    for="observation-start"
                    class="block text-sm font-medium text-gray-700"
                  >
                    Start of observations
                  </label>
                  <input
                    id="observation-start"
                    type="datetime-local"
                    class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-gray-900 focus:border-gray-900 sm:text-sm"
                  />
                </div>
              </div>
            </div>
            <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
              <button
                type="submit"
                class="bg-gray-800 border border-transparent rounded-md shadow-sm py-2 px-4 inline-flex justify-center text-sm font-medium text-white hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-900"
              >
                Save
              </button>
            </div>
          </div>
        </form>
      </section>
    </>
  );
};

export default General;
