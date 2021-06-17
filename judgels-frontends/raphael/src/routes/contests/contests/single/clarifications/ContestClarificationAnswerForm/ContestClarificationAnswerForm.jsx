import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { Required } from '../../../../../../components/forms/validations';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';

export default function ContestClarificationAnswerForm({ onSubmit, isLoading, onCancel }) {
  const answerField = {
    name: 'answer',
    label: 'Answer',
    rows: 5,
    validate: Required,
    autoFocus: true,
  };

  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextArea} {...answerField} />
          <ActionButtons leftAlign>
            <Button type="submit" text="Answer" intent={Intent.PRIMARY} loading={isLoading} />
            <Button text="Cancel" onClick={onCancel} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
