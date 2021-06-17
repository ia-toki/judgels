import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { composeValidators, EmailAddress, Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';

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
