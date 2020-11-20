import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm, Form } from 'redux-form';

import { FormTextInput } from '../forms/FormTextInput/FormTextInput';

import './SearchBox.css';

function SearchBoxForm({ handleSubmit, submitting, isLoading }) {
  const contentField = {
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
      loading={isLoading || submitting}
    />
  );

  return (
    <Form onSubmit={handleSubmit}>
      {fields}
      {submitButton}
    </Form>
  );
}

export default reduxForm({
  form: 'search-box',
  touchOnBlur: false,
  enableReinitialize: true,
})(SearchBoxForm);
