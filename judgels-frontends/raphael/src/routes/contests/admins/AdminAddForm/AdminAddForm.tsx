import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface AdminAddFormData {
  usernames: string;
}

export interface AdminAddFormProps extends InjectedFormProps<AdminAddFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const AdminAddForm = (props: AdminAddFormProps) => {
  const usernamesField: any = {
    name: 'usernames',
    label: 'Usernames',
    labelHelper: '(one username per line, max 100 users)',
    rows: 20,
    isCode: true,
    validate: [Required, Max100Lines],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...usernamesField} />;
  const submitButton = <Button type="submit" text="Add" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<AdminAddFormData>({
  form: 'admin-add',
  touchOnBlur: false,
})(AdminAddForm);
