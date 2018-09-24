import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';

export interface ContestClarificationAnswerCreateFormData {
  answererJid: string;
  answer: string;
}

export interface ContestClarificationAnswerCreateFormProps extends InjectedFormProps<ContestClarificationAnswerCreateFormData> {
  contestJid: string;
  problemJids: string[];
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };

  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ContestClarificationAnswerCreateForm = (props: ContestClarificationAnswerCreateFormProps) => {
  const answerField: any = {
    name: 'answer',
    label: 'Answer',
    validate: [Required],
  };

  const fields = (
    <>
      <Field component={FormTextArea} {...answerField} />
    </>
  );

  const submitButton = <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ContestClarificationAnswerCreateFormData>({ form: 'contest-clarification-create' })(
  ContestClarificationAnswerCreateForm
);
