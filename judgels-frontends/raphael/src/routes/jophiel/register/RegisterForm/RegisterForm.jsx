import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';
import { Link } from 'react-router-dom';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormRecaptcha } from '../../../../components/forms/FormRecaptcha/FormRecaptcha';
import { ConfirmPassword, EmailAddress, Required, Username } from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';

import './RegisterForm.css';

const usernameField = {
  name: 'username',
  label: 'Username',
  required: true,
  validate: [Required, Username],
};

const nameField = {
  name: 'name',
  label: 'Name',
  validate: [Required],
};

const emailField = {
  name: 'email',
  label: 'Email',
  validate: [Required, EmailAddress],
};

const passwordField = {
  name: 'password',
  label: 'Password',
  type: 'password',
  validate: [Required],
};

const confirmPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm password',
  type: 'password',
  validate: [Required, ConfirmPassword],
};

function RegisterForm({ handleSubmit, submitting, useRecaptcha, recaptchaSiteKey }) {
  let recaptchaChallengeField;
  if (useRecaptcha) {
    const recaptchaField = {
      name: 'recaptchaResponse',
      siteKey: recaptchaSiteKey,
      validate: [Required],
    };
    recaptchaChallengeField = <Field component={FormRecaptcha} {...recaptchaField} />;
  }

  return (
    <form onSubmit={handleSubmit}>
      <Field component={FormTextInput} {...usernameField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextInput} {...emailField} />
      <Field component={FormTextInput} {...passwordField} />
      <Field component={FormTextInput} {...confirmPasswordField} />
      {recaptchaChallengeField}

      <HorizontalDivider />

      <div className="form-login__actions">
        <Button type="submit" text="Register" intent={Intent.PRIMARY} loading={submitting} />
        <p className="form-login__actions-register">
          Have an account already? <Link to="/login">Log in now</Link>
        </p>
      </div>
    </form>
  );
}

export default reduxForm({ form: 'register', touchOnBlur: false })(RegisterForm);
