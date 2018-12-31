import * as React from 'react';

import { stringify, parse } from 'query-string';
import { push } from 'react-router-redux';
import { withRouter, RouteComponentProps } from 'react-router';
import { connect } from 'react-redux';

import SearchBoxForm, { SearchBoxFormData } from './SearchBoxForm';

interface SearchBoxRouteProps extends RouteComponentProps<{}> {
  onAppendRoute: (queries: any) => any;
}

export interface SearchBoxProps {
  onRouteChange: (content: string, prevQueries: any) => any;
  initialValue?: string;
  isLoading?: boolean;
}

const SearchBoxContainer = (props: SearchBoxProps & SearchBoxRouteProps) => {
  const handleSubmit = (data: SearchBoxFormData) => {
    const queries = parse(props.location.search);
    props.onAppendRoute(props.onRouteChange(data.content, queries));
  };

  const { initialValue: content, isLoading } = props;

  const formProps = {
    isLoading,
    initialValues: {
      content,
    },
  };

  return <SearchBoxForm onSubmit={handleSubmit} {...formProps} />;
};

function createSearchBox() {
  const mapDispatchToProps = {
    onAppendRoute: queries => push({ search: stringify(queries) }),
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(SearchBoxContainer));
}

export default createSearchBox();
