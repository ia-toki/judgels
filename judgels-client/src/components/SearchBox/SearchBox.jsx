import { parse, stringify } from 'query-string';
import { useHistory, useLocation } from 'react-router-dom';

import SearchBoxForm from './SearchBoxForm';

export default function SearchBoxContainer({ onRouteChange, initialValue, isLoading }) {
  const location = useLocation();
  const history = useHistory();

  const handleSubmit = ({ content }) => {
    const queries = parse(location.search);
    const newQueries = onRouteChange(content, queries);
    history.push({ search: stringify(newQueries) });
  };

  const formProps = {
    isLoading,
    initialValues: {
      content: initialValue,
    },
  };

  return <SearchBoxForm onSubmit={handleSubmit} {...formProps} />;
}
