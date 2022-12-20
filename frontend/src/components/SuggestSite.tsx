import {
  Component,
  createResource,
  createSignal,
  useTransition,
} from "solid-js";
import SiteService from "../services/SiteService";
import ComboBox from "./SuggestionInput";

type SuggestSiteProps = {
  id: string;
  value?: string;
  setValue: (value: string) => void;
  wrapperClass?: string;
  error?: string;
};

const SuggestSite: Component<SuggestSiteProps> = (props) => {
  const [, start] = useTransition();
  const siteService = SiteService();

  const [searchTerm, setSearchTerm] = createSignal("");

  const [siteData] = createResource(
    () => "name=" + searchTerm(),
    siteService.all
  );
  const sites = () => siteData()?._embedded?.sites.map((c) => c.name) ?? [];

  return (
    <ComboBox
      id={props.id}
      wrapperClass={props.wrapperClass}
      label="Site"
      placeholder="Site"
      value={props.value}
      setValue={props.setValue}
      searchTerm={searchTerm()}
      setSearchTerm={(value) => {
        start(() => setSearchTerm(value));
      }}
      options={sites()}
      error={props.error}
    />
  );
};

export default SuggestSite;
