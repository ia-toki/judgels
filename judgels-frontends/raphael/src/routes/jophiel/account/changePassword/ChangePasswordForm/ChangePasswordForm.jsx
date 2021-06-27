import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextInput } from '../../../../../components/forms/FormTextInput/FormTextInput';
import { composeValidators, ConfirmPassword, Required } from '../../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../../components/HorizontalDivider/HorizontalDivider';

const oldPasswordField = {
  name: 'oldPassword',
  label: 'Old password',
  inputType: 'password',
  validate: Required,
};

const newPasswordField = {
  name: 'password',
  label: 'New password',
  inputType: 'password',
  validate: Required,
};

const confirmNewPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm new password',
  inputType: 'password',
  validate: composeValidators(Required, ConfirmPassword),
};

export default function ChangePasswordForm({ onSubmit }) {
  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextInput} {...oldPasswordField} />
          <Field component={FormTextInput} {...newPasswordField} />
          <Field component={FormTextInput} {...confirmNewPasswordField} />

          <HorizontalDivider />

          <Button type="submit" text="Change password" intent={Intent.PRIMARY} loading={submitting} />
        </form>
      )}
    </Form>
  );
}
