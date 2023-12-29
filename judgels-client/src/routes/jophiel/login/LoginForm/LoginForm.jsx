import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';
import { Link } from 'react-router-dom';

import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';
import { isTLX } from '../../../../conf';

import './LoginForm.scss';

const usernameOrEmailField = {
  name: 'usernameOrEmail',
  label: 'Username or Email',
  validate: Required,
};

const passwordField = {
  name: 'password',
  label: 'Password',
  inputType: 'password',
  validate: Required,
};

export default function LoginForm({ onSubmit }) {
  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTextInput} {...usernameOrEmailField} />
          <Field component={FormTextInput} {...passwordField} />
          {isTLX() && (
            <p className="form-login__actions-forgot-password">
              <Link to="/forgot-password">Forgot your password?</Link>
            </p>
          )}

          <HorizontalDivider />

          <div className="form-login__actions">
            <Button type="submit" text="Log in" intent={Intent.PRIMARY} loading={submitting} />
            {isTLX() && (
              <p className="form-login__actions-register">
                Don't have account? <Link to="/register">Register now</Link>
              </p>
            )}
          </div>
        </form>
      )}
    </Form>
  );
}
