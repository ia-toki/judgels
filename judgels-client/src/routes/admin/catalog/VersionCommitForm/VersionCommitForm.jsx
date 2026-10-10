import { Button, Callout, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTableTextArea } from '../../../../components/forms/FormTableTextArea/FormTableTextArea';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { Required } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const keyStyles = { width: '250px' };

const titleField = {
  keyStyles,
  name: 'title',
  label: 'Title',
  validate: Required,
};

const descriptionField = {
  keyStyles,
  name: 'description',
  label: 'Description',
};

export default function VersionCommitForm({ onSubmit, disabled }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, submitting, submitError }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            {submitError && <Callout intent={Intent.DANGER}>{submitError}</Callout>}
            <HTMLTable striped>
              <tbody>
                <Field component={FormTableTextInput} {...titleField} />
                <Field component={FormTableTextArea} {...descriptionField} />
              </tbody>
            </HTMLTable>
            <ActionButtons justifyContent="end">
              <Button type="submit" text="Commit" intent={Intent.PRIMARY} disabled={disabled} loading={submitting} />
            </ActionButtons>
          </form>
        </Flex>
      )}
    </Form>
  );
}
