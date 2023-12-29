import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { EmailAddress, Required, composeValidators } from '../../../../components/forms/validations';

const emailField = {
  name: 'email',
  label: 'Email',
  validate: composeValidators(Required, EmailAddress),
};

export default function ForgotPasswordForm({ onSubmit }) {
  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextInput} {...emailField} />

          <HorizontalDivider />

          <ActionButtons>
            <Button type="submit" text="Request to reset password" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
