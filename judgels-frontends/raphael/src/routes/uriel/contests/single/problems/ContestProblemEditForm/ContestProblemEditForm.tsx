import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';

import { ValidProblemsSetData } from '../modules/contestProblemValidations';

export interface ContestProblemEditFormData {
  problems: string;
}

export interface ContestProblemEditFormProps extends InjectedFormProps<ContestProblemEditFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ContestProblemEditForm = (props: ContestProblemEditFormProps) => {
  const problemsField: any = {
    name: 'problems',
    label: 'Problems',
    labelHelper: '(one problem per line, max 100 problems)',
    rows: 10,
    validate: [ValidProblemsSetData],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...problemsField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ContestProblemEditFormData>({
  form: 'contest-problem-edit',
  touchOnBlur: false,
})(ContestProblemEditForm);
