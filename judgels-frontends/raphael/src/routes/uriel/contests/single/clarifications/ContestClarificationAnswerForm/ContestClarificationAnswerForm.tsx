import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';

export interface ContestClarificationAnswerFormData {
  answer: string;
}

const ContestClarificationAnswerForm = (props: InjectedFormProps<ContestClarificationAnswerFormData>) => {
  const answerField: any = {
    name: 'answer',
    label: 'Answer',
    validate: [Required],
  };

  return (
    <form onSubmit={props.handleSubmit}>
      <Field component={FormTextArea} {...answerField} />
      <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={props.submitting} />
    </form>
  );
};

export default reduxForm<ContestClarificationAnswerFormData>({ form: 'contest-clarification-answer' })(
  ContestClarificationAnswerForm
);
