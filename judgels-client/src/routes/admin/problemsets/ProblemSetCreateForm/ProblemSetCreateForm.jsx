import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormDateInput } from '../../../../components/forms/FormDateInput/FormDateInput';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

export default function ProblemSetCreateForm({ onSubmit, initialValues, onCancel }) {
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
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        return (
          <form onSubmit={handleSubmit}>
            {fields}
            <ActionButtons justifyContent="end">
              <Button text="Cancel" disabled={submitting} onClick={onCancel} />
              <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />
            </ActionButtons>
          </form>
        );
      }}
    </Form>
  );
}
