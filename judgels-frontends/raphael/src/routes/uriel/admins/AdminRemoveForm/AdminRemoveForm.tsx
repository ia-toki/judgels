import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface AdminRemoveFormData {
  usernames: string;
}

export interface AdminRemoveFormProps extends InjectedFormProps<AdminRemoveFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const AdminRemoveForm = (props: AdminRemoveFormProps) => {
  const usernamesField: any = {
    name: 'usernames',
    label: 'Usernames',
    labelHelper: '(one username per line, max 100 users)',
    rows: 20,
    validate: [Required],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...usernamesField} />;
  const submitButton = <Button type="submit" text="Remove" intent={Intent.DANGER} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<AdminRemoveFormData>({
  form: 'admin-remove',
  touchOnBlur: false,
})(AdminRemoveForm);
