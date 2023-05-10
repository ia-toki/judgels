import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextInput } from '../forms/FormTextInput/FormTextInput';

import './SearchBox.scss';

export default function SearchBoxForm({ onSubmit, initialValues, isLoading }) {
  const contentField = {
    name: 'content',
  };

  const fields = (
    <div className="search-box-input">
      <Field component={FormTextInput} {...contentField} />
    </div>
  );

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
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
          <form onSubmit={handleSubmit}>
            {fields}
            {submitButton}
          </form>
        );
      }}
    </Form>
  );
}
