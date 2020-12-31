import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { EmailAddress, Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';

const emailField = {
  name: 'email',
  label: 'Email',
  validate: [Required, EmailAddress],
};

function ForgotPasswordForm({ handleSubmit, submitting }) {
  return (
    <form onSubmit={handleSubmit}>
      <Field component={FormTextInput} {...emailField} />

      <HorizontalDivider />

      <ActionButtons>
        <Button type="submit" text="Request to reset password" intent={Intent.PRIMARY} loading={submitting} />
      </ActionButtons>
    </form>
  );
}

export default reduxForm({ form: 'forgotPassword', touchOnBlur: false })(ForgotPasswordForm);
