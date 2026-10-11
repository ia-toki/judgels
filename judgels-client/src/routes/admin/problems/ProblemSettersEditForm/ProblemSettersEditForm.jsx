import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { ProblemSetterRole } from '../../../../modules/api/problem';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const keyStyles = { width: '250px' };

const setterInputHelpers = {
  [ProblemSetterRole.Developer]: 'Comma-separated usernames. Fill only if different from writers.',
};

export default function ProblemSettersEditForm({ onSubmit, initialValues, setterFields, onCancel }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <HTMLTable striped>
              <tbody>
                {setterFields.map(({ role, title }) => (
                  <Field
                    key={role}
                    component={FormTableTextInput}
                    keyStyles={keyStyles}
                    name={role}
                    label={title}
                    inputHelper={setterInputHelpers[role] || 'Comma-separated usernames.'}
                  />
                ))}
              </tbody>
            </HTMLTable>
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
