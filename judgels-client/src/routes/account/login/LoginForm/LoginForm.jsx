import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import { Field, Form } from 'react-final-form';

import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';
import { isUserRegistrationEnabled, isUserResetPasswordEnabled } from '../../../../modules/webConfig';

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
          {isUserResetPasswordEnabled() && (
            <p className="login-form__forgot-password">
              <Link to="/forgot-password">Forgot your password?</Link>
            </p>
          )}

          <HorizontalDivider />

          <Flex>
            <Button type="submit" text="Log in" intent={Intent.PRIMARY} loading={submitting} />
            {isUserRegistrationEnabled() && (
              <p className="login-form__register">
                Don't have account? <Link to="/register">Register now</Link>
              </p>
            )}
          </Flex>
        </form>
      )}
    </Form>
  );
}
