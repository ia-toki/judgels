import { useLocation, useNavigate } from '@tanstack/react-router';

import SearchBoxForm from './SearchBoxForm';

export default function SearchBoxContainer({ onRouteChange, initialValue, isLoading }) {
  const location = useLocation();
  const navigate = useNavigate();

  const handleSubmit = ({ content }) => {
    const newQueries = onRouteChange(content, location.search);
    navigate({ search: newQueries });
  };

  const formProps = {
    isLoading,
    initialValues: {
      content: initialValue,
    },
  };

  return <SearchBoxForm onSubmit={handleSubmit} {...formProps} />;
}
