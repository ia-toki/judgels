import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { composeValidators, ConfirmPassword, Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';

const newPasswordField = {
  name: 'password',
  label: 'New Password',
  type: 'password',
  validate: Required,
};

const confirmNewPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm New Password',
  type: 'password',
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
