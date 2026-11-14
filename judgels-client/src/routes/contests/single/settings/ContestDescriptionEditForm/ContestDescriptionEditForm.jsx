import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../../components/ActionButtons/ActionButtons';
import { FormRichTextArea } from '../../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormUnsavedChangesAlert } from '../../../../../components/forms/UnsavedChangesAlert/UnsavedChangesAlert';

export default function ContestDescriptionEditForm({ onSubmit, initialValues, onCancel }) {
  const descriptionField = {
    name: 'description',
    rows: 16,
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <FormUnsavedChangesAlert />
          <Field component={FormRichTextArea} {...descriptionField} />
          <hr />
          <ActionButtons justifyContent="end">
            <Button text="Cancel" disabled={submitting} onClick={onCancel} />
            <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
