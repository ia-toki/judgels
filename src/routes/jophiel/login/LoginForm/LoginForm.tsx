import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';

import './LoginForm.css';

export interface LoginFormData {
  username: string;
  password: string;
}

const usernameField: any = {
  name: 'username',
  label: 'Username',
  validate: [Required],
} as any;

const passwordField: any = {
  name: 'password',
  label: 'Password',
  type: 'password',
  validate: [Required],
};

const LoginForm = (props: InjectedFormProps<LoginFormData>) => (
  <form onSubmit={props.handleSubmit}>
    <Field component={FormTextInput} {...usernameField} />
    <Field component={FormTextInput} {...passwordField} />
    <p className="form-login__actions-forgot-password">
      <Link to="/forgot-password">Forgot your password?</Link>
    </p>

    <HorizontalDivider />

    <div className="form-login__actions">
      <Button type="submit" text="Log in" intent={Intent.PRIMARY} loading={props.submitting} />
      <p className="form-login__actions-register">
        Don't have account? <Link to="/register">Register now</Link>
      </p>
    </div>
  </form>
);

export default reduxForm<LoginFormData>({ form: 'login' })(LoginForm);
