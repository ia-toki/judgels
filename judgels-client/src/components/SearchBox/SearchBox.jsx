import { parse, stringify } from 'query-string';
import { useLocation, useNavigate } from 'react-router';

import SearchBoxForm from './SearchBoxForm';

export default function SearchBoxContainer({ onRouteChange, initialValue, isLoading }) {
  const location = useLocation();
  const navigate = useNavigate();

  const handleSubmit = ({ content }) => {
    const queries = parse(location.search);
    const newQueries = onRouteChange(content, queries);
    navigate({ search: stringify(newQueries) });
  };

  const formProps = {
    isLoading,
    initialValues: {
      content: initialValue,
    },
  };

  return <SearchBoxForm onSubmit={handleSubmit} {...formProps} />;
}
