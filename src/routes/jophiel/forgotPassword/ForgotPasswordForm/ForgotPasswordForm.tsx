import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { EmailAddress, Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';

export interface ForgotPasswordFormData {
  email: string;
}

const emailField = {
  name: 'email',
  label: 'Email',
  validate: [Required, EmailAddress],
};

const ForgotPasswordForm = (props: InjectedFormProps<ForgotPasswordFormData>) => (
  <form onSubmit={props.handleSubmit}>
    <Field component={FormTextInput} {...emailField} />

    <HorizontalDivider />

    <ActionButtons>
      <Button type="submit" text="Request to reset password" intent={Intent.PRIMARY} loading={props.submitting} />
    </ActionButtons>
  </form>
);

export default reduxForm<ForgotPasswordFormData>({ form: 'forgotPassword' })(ForgotPasswordForm);
