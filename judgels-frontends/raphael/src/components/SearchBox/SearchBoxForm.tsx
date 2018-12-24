import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm, Form } from 'redux-form';

import { FormTextInput } from 'components/forms/FormTextInput/FormTextInput';

import './SearchBox.css';

export interface SearchBoxFormData {
  content: string;
}

export interface SearchBoxFormProps extends InjectedFormProps<SearchBoxFormData> {
  isLoading: boolean;
}

const SearchBoxForm = (props: SearchBoxFormProps) => {
  const contentField: any = {
    name: 'content',
  };

  const fields = (
    <div className="search-box-input">
      <Field component={FormTextInput} {...contentField} />
    </div>
  );
  const submitButton = (
    <Button
      className="search-box-button"
      type="submit"
      text="Search"
      intent={Intent.PRIMARY}
      loading={props.isLoading || props.submitting}
    />
  );

  return (
    <Form onSubmit={props.handleSubmit}>
      {fields}
      {submitButton}
    </Form>
  );
};

export default reduxForm<SearchBoxFormData>({
  form: 'search-box',
  touchOnBlur: false,
})(SearchBoxForm);
