import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface ChapterProblemEditFormData {
  problems: string;
}

export interface ChapterProblemEditFormProps extends InjectedFormProps<ChapterProblemEditFormData> {
  validator: (value: string) => any;
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ChapterProblemEditForm = (props: ChapterProblemEditFormProps) => {
  const problemsField: any = {
    name: 'problems',
    label: 'Problems',
    labelHelper: '(one problem per line, max 100 problems)',
    rows: 10,
    isCode: true,
    validate: [Required, Max100Lines, props.validator],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...problemsField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ChapterProblemEditFormData>({
  form: 'chapter-problems-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(ChapterProblemEditForm);
