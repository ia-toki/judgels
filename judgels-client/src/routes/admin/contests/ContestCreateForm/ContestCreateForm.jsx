import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const slugField = {
  name: 'slug',
  label: 'Slug',
  validate: composeValidators(Required, Slug),
  autoFocus: true,
};

export default function ContestCreateForm({ onSubmit, onCancel }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextInput} {...slugField} />
          <ActionButtons justifyContent="end">
            <Button text="Cancel" disabled={submitting} onClick={onCancel} />
            <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
