import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { ConfirmPassword, Required, composeValidators } from '../../../../components/forms/validations';

const newPasswordField = {
  name: 'password',
  label: 'New Password',
  inputType: 'password',
  validate: Required,
};

const confirmNewPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm New Password',
  inputType: 'password',
  validate: composeValidators(Required, ConfirmPassword),
};

export default function ResetPasswordForm({ onSubmit }) {
  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextInput} {...newPasswordField} />
          <Field component={FormTextInput} {...confirmNewPasswordField} />

          <HorizontalDivider />

          <ActionButtons>
            <Button type="submit" text="Reset password" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
