import {
  Component,
  createResource,
  createSignal,
  useTransition,
} from "solid-js";
import CompanyService from "../services/CompanyService";
import ComboBox from "./SuggestionInput";

type SuggestCompanyProps = {
  id: string;
  value?: string;
  setValue: (value: string) => void;
  wrapperClass?: string;
  error?: string;
};

const SuggestCompany: Component<SuggestCompanyProps> = (props) => {
  const [, start] = useTransition();
  const companyService = CompanyService();

  const [searchTerm, setSearchTerm] = createSignal("");

  const [companyData] = createResource(
    () => "name=" + searchTerm(),
    companyService.all
  );
  const companies = () =>
    companyData()?._embedded?.companies.map((c) => c.name) ?? [];

  return (
    <ComboBox
      id={props.id}
      wrapperClass={props.wrapperClass}
      label="Observed company"
      placeholder="Observed company"
      value={props.value}
      setValue={props.setValue}
      searchTerm={searchTerm()}
      setSearchTerm={(value) => {
        start(() => setSearchTerm(value));
      }}
      options={companies()}
      error={props.error}
    />
  );
};

export default SuggestCompany;
