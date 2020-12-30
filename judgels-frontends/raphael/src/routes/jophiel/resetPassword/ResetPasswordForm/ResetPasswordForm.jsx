import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { ConfirmPassword, Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';

const newPasswordField = {
  name: 'password',
  label: 'New Password',
  type: 'password',
  validate: [Required],
};

const confirmNewPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm New Password',
  type: 'password',
  validate: [Required, ConfirmPassword],
};

function ResetPasswordForm({ handleSubmit, submitting }) {
  return (
    <form onSubmit={handleSubmit}>
      <Field component={FormTextInput} {...newPasswordField} />
      <Field component={FormTextInput} {...confirmNewPasswordField} />

      <HorizontalDivider />

      <ActionButtons>
        <Button type="submit" text="Reset password" intent={Intent.PRIMARY} loading={submitting} />
      </ActionButtons>
    </form>
  );
}

export default reduxForm({ form: 'resetPassword', touchOnBlur: false })(ResetPasswordForm);
