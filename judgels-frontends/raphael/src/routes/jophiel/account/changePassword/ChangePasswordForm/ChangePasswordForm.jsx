import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { FormTextInput } from '../../../../../components/forms/FormTextInput/FormTextInput';
import { ConfirmPassword, Required } from '../../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../../components/HorizontalDivider/HorizontalDivider';

const oldPasswordField = {
  name: 'oldPassword',
  label: 'Old password',
  type: 'password',
  validate: [Required],
};

const newPasswordField = {
  name: 'password',
  label: 'New password',
  type: 'password',
  validate: [Required],
};

const confirmNewPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm new password',
  type: 'password',
  validate: [Required, ConfirmPassword],
};

function ChangePasswordForm({ handleSubmit, submitting }) {
  return (
    <form onSubmit={handleSubmit}>
      <Field component={FormTextInput} {...oldPasswordField} />
      <Field component={FormTextInput} {...newPasswordField} />
      <Field component={FormTextInput} {...confirmNewPasswordField} />

      <HorizontalDivider />

      <Button type="submit" text="Change password" intent={Intent.PRIMARY} loading={submitting} />
    </form>
  );
}

export default reduxForm({ form: 'login', touchOnBlur: false })(ChangePasswordForm);
