import { stringify, parse } from 'query-string';
import { push } from 'connected-react-router';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import SearchBoxForm from './SearchBoxForm';

function SearchBoxContainer({ location, onAppendRoute, onRouteChange, initialValue, isLoading }) {
  const handleSubmit = ({ content }) => {
    const queries = parse(location.search);
    onAppendRoute(onRouteChange(content, queries));
  };

  const formProps = {
    isLoading,
    initialValues: {
      content: initialValue,
    },
  };

  return <SearchBoxForm onSubmit={handleSubmit} {...formProps} />;
}

const mapDispatchToProps = {
  onAppendRoute: queries => push({ search: stringify(queries) }),
};
export default withRouter(connect(undefined, mapDispatchToProps)(SearchBoxContainer));
