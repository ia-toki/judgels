import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';
import { Link } from 'react-router-dom';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormRecaptcha } from '../../../../components/forms/FormRecaptcha/FormRecaptcha';
import { ConfirmPassword, EmailAddress, Required, Username } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';

import './RegisterForm.css';

export interface RegisterFormData {
  username: string;
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
  recaptchaResponse?: string;
}

const usernameField: any = {
  name: 'username',
  label: 'Username',
  required: true,
  validate: [Required, Username],
};

const nameField: any = {
  name: 'name',
  label: 'Name',
  validate: [Required],
};

const emailField: any = {
  name: 'email',
  label: 'Email',
  validate: [Required, EmailAddress],
};

const passwordField: any = {
  name: 'password',
  label: 'Password',
  type: 'password',
  validate: [Required],
};

const confirmPasswordField: any = {
  name: 'confirmPassword',
  label: 'Confirm password',
  type: 'password',
  validate: [Required, ConfirmPassword],
};

export interface RegisterFormProps extends InjectedFormProps<RegisterFormData> {
  useRecaptcha: boolean;
  recaptchaSiteKey?: string;
}

const RegisterForm = (props: RegisterFormProps) => {
  let recaptchaChallengeField;
  if (props.useRecaptcha) {
    const recaptchaField: any = {
      name: 'recaptchaResponse',
      siteKey: props.recaptchaSiteKey!,
      validate: [Required],
    };
    recaptchaChallengeField = <Field component={FormRecaptcha} {...recaptchaField} />;
  }

  return (
    <form onSubmit={props.handleSubmit}>
      <Field component={FormTextInput} {...usernameField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextInput} {...emailField} />
      <Field component={FormTextInput} {...passwordField} />
      <Field component={FormTextInput} {...confirmPasswordField} />
      {recaptchaChallengeField}

      <HorizontalDivider />

      <div className="form-login__actions">
        <Button type="submit" text="Register" intent={Intent.PRIMARY} loading={props.submitting} />
        <p className="form-login__actions-register">
          Have an account already? <Link to="/login">Log in now</Link>
        </p>
      </div>
    </form>
  );
};

export default reduxForm<RegisterFormData>({ form: 'register' })(RegisterForm);
