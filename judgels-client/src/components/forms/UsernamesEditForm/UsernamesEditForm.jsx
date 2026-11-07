import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { withSubmissionError } from '../../../modules/form/submissionError';
import { ActionButtons } from '../../ActionButtons/ActionButtons';
import { FormTextArea } from '../FormTextArea/FormTextArea';
import { FormUnsavedChangesAlert } from '../UnsavedChangesAlert/UnsavedChangesAlert';

export function parseUsernames(text) {
  const usernames = (text || '')
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0);
  return [...new Set(usernames)];
}

export function UsernamesEditForm({ label, onSubmit, initialValues, validateUsernames, onCancel }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <FormUnsavedChangesAlert />
            <Field
              component={FormTextArea}
              name="usernames"
              label={label}
              labelHelper="(one username per line)"
              rows={10}
              isCode
              validate={validateUsernames}
              autoFocus
            />
            <ActionButtons justifyContent="end">
              <Button text="Cancel" disabled={submitting} onClick={onCancel} />
              <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
            </ActionButtons>
          </form>
        </Flex>
      )}
    </Form>
  );
}
