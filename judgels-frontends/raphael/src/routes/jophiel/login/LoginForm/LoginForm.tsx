import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { APP_CONFIG, Mode } from '../../../../conf';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';

import './LoginForm.css';

export interface LoginFormData {
  usernameOrEmail: string;
  password: string;
}

const usernameOrEmailField: any = {
  name: 'usernameOrEmail',
  label: 'Username or Email',
  validate: [Required],
  autoFocus: true,
};

const passwordField: any = {
  name: 'password',
  label: 'Password',
  type: 'password',
  validate: [Required],
};

const LoginForm = (props: InjectedFormProps<LoginFormData>) => (
  <form onSubmit={props.handleSubmit}>
    <Field component={FormTextInput} {...usernameOrEmailField} />
    <Field component={FormTextInput} {...passwordField} />
    {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
      <p className="form-login__actions-forgot-password">
        <Link to="/forgot-password">Forgot your password?</Link>
      </p>
    )}

    <HorizontalDivider />

    <div className="form-login__actions">
      <Button type="submit" text="Log in" intent={Intent.PRIMARY} loading={props.submitting} />
      {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
        <p className="form-login__actions-register">
          Don't have account? <Link to="/register">Register now</Link>
        </p>
      )}
    </div>
  </form>
);

export default reduxForm<LoginFormData>({ form: 'login', touchOnBlur: false })(LoginForm);
