import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';

export default function ChapterCreateForm({ onSubmit, onCancel }) {
  const nameField = {
    name: 'name',
    label: 'Name',
    validate: Required,
    autoFocus: true,
  };

  const fields = <Field component={FormTextInput} {...nameField} />;

  return (
    <Form onSubmit={onSubmit}>
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
