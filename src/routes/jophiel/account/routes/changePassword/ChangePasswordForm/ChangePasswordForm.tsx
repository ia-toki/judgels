import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTextInput } from '../../../../../../components/forms/FormTextInput/FormTextInput';
import { ConfirmPassword, Required } from '../../../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../../../components/HorizontalDivider/HorizontalDivider';

export interface ChangePasswordFormData {
  oldPassword: string;
  password: string;
  confirmPassword: string;
}

const oldPasswordField: any = {
  name: 'oldPassword',
  label: 'Old password',
  type: 'password',
  validate: [Required],
};

const newPasswordField: any = {
  name: 'password',
  label: 'New password',
  type: 'password',
  validate: [Required],
};

const confirmNewPasswordField: any = {
  name: 'confirmPassword',
  label: 'Confirm new password',
  type: 'password',
  validate: [Required, ConfirmPassword],
};

const ChangePasswordForm = (props: InjectedFormProps<ChangePasswordFormData>) => (
  <form onSubmit={props.handleSubmit}>
    <Field component={FormTextInput} {...oldPasswordField} />
    <Field component={FormTextInput} {...newPasswordField} />
    <Field component={FormTextInput} {...confirmNewPasswordField} />

    <HorizontalDivider />

    <Button type="submit" text="Change password" intent={Intent.PRIMARY} loading={props.submitting} />
  </form>
);

export default reduxForm<ChangePasswordFormData>({ form: 'login' })(ChangePasswordForm);
