import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { withSubmissionError } from '../../../../modules/form/submissionError';

export default function ProblemPartnersEditForm({ onSubmit, initialValues, validateCsv, onCancel }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <div>
              <p>
                One partner per row, as <code>username[,permission]</code>, where <code>permission</code> is either:
              </p>
              <ul>
                <li>
                  <code>UPDATE</code> (the default): the user can view and update the problem
                </li>
                <li>
                  <code>VIEW</code>: the user can only view the problem
                </li>
              </ul>
            </div>
            <Field
              component={FormTextArea}
              name="csv"
              label="Partners"
              rows={10}
              isCode
              validate={validateCsv}
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
