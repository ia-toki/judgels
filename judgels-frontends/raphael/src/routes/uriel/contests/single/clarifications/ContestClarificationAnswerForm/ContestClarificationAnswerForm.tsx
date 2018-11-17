import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';
import { ActionButtons } from 'components/ActionButtons/ActionButtons';

export interface ContestClarificationAnswerFormData {
  answer: string;
}

export interface ContestClarificationAnswerFormProps extends InjectedFormProps<ContestClarificationAnswerFormData> {
  isLoading: boolean;
  onCancel: () => void;
}

const ContestClarificationAnswerForm = (props: ContestClarificationAnswerFormProps) => {
  const answerField: any = {
    name: 'answer',
    label: 'Answer',
    rows: 5,
    validate: [Required],
    autoFocus: true,
  };

  return (
    <form onSubmit={props.handleSubmit}>
      <Field component={FormTextArea} {...answerField} />
      <ActionButtons leftAlign>
        <Button type="submit" text="Answer" intent={Intent.PRIMARY} loading={props.isLoading} />
        <Button text="Cancel" onClick={props.onCancel} />
      </ActionButtons>
    </form>
  );
};

export default reduxForm<ContestClarificationAnswerFormData>({
  form: 'contest-clarification-answer',
  touchOnBlur: false,
})(ContestClarificationAnswerForm);
