import { Button, Intent } from '@blueprintjs/core';
import { Link } from 'react-router-dom';
import { Field, reduxForm } from 'redux-form';

import { APP_CONFIG, Mode } from '../../../../conf';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';

import './LoginForm.scss';

const usernameOrEmailField = {
  name: 'usernameOrEmail',
  label: 'Username or Email',
  validate: [Required],
  autoFocus: true,
};

const passwordField = {
  name: 'password',
  label: 'Password',
  type: 'password',
  validate: [Required],
};

const LoginForm = ({ handleSubmit, submitting }) => (
  <form onSubmit={handleSubmit}>
    <Field component={FormTextInput} {...usernameOrEmailField} />
    <Field component={FormTextInput} {...passwordField} />
    {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
      <p className="form-login__actions-forgot-password">
        <Link to="/forgot-password">Forgot your password?</Link>
      </p>
    )}

    <HorizontalDivider />

    <div className="form-login__actions">
      <Button type="submit" text="Log in" intent={Intent.PRIMARY} loading={submitting} />
      {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
        <p className="form-login__actions-register">
          Don't have account? <Link to="/register">Register now</Link>
        </p>
      )}
    </div>
  </form>
);

export default reduxForm({ form: 'login', touchOnBlur: false })(LoginForm);
