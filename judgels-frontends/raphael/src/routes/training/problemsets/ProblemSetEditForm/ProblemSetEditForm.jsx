import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { composeValidators, Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormDateInput } from '../../../../components/forms/FormDateInput/FormDateInput';

export default function ProblemSetEditForm({ onSubmit, initialValues, renderFormComponents }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
  };
  const nameField = {
    name: 'name',
    label: 'Name',
    validate: Required,
  };
  const archiveSlugField = {
    name: 'archiveSlug',
    label: 'Archive slug',
    validate: Required,
  };
  const descriptionField = {
    name: 'description',
    label: 'Description',
    rows: 15,
  };
  const contestTimeField = {
    name: 'contestTime',
    label: 'Contest time',
    validate: Required,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextInput} {...archiveSlugField} />
      <Field component={FormDateInput} {...contestTimeField} />
      <Field component={FormRichTextArea} {...descriptionField} />
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
