import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../../../../../../components/forms/validations';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';

function ContestClarificationAnswerForm({ handleSubmit, isLoading, onCancel }) {
  const answerField = {
    name: 'answer',
    label: 'Answer',
    rows: 5,
    validate: [Required],
    autoFocus: true,
  };

  return (
    <form onSubmit={handleSubmit}>
      <Field component={FormTextArea} {...answerField} />
      <ActionButtons leftAlign>
        <Button type="submit" text="Answer" intent={Intent.PRIMARY} loading={isLoading} />
        <Button text="Cancel" onClick={onCancel} />
      </ActionButtons>
    </form>
  );
}

export default reduxForm({
  form: 'contest-clarification-answer',
  touchOnBlur: false,
})(ContestClarificationAnswerForm);
