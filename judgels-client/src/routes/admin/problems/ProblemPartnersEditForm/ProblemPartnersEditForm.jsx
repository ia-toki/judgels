import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { withSubmissionError } from '../../../../modules/form/submissionError';

export default function ProblemPartnersEditForm({ onSubmit, initialValues, validateUsernames, onCancel }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <Field
              component={FormTextArea}
              name="usernames"
              label="Partners"
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
