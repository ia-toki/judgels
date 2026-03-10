import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

export default function ArchiveCreateForm({ onSubmit, renderFormComponents }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
    autoFocus: true,
  };

  const nameField = {
    name: 'name',
    label: 'Name',
    validate: Required,
  };

  const categoryField = {
    name: 'category',
    label: 'Category',
    validate: Required,
  };

  const descriptionField = {
    name: 'description',
    label: 'Description',
    rows: 5,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextInput} {...categoryField} />
      <Field component={FormTextArea} {...descriptionField} />
    </>
  );

  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
