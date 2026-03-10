import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';

export default function ChapterEditForm({ onSubmit, initialValues, renderFormComponents }) {
  const nameField = {
    name: 'name',
    label: 'Name',
    validate: Required,
    autoFocus: true,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...nameField} />
    </>
  );

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Update" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
