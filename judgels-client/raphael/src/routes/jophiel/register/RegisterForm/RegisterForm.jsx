import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';
import { Link } from 'react-router-dom';

import { withSubmissionError } from '../../../../modules/form/submissionError';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormRecaptcha } from '../../../../components/forms/FormRecaptcha/FormRecaptcha';
import {
  composeValidators,
  ConfirmPassword,
  EmailAddress,
  Required,
  Username,
} from '../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';

import './RegisterForm.scss';

const usernameField = {
  name: 'username',
  label: 'Username',
  required: true,
  validate: composeValidators(Required, Username),
};

const nameField = {
  name: 'name',
  label: 'Name',
  validate: Required,
};

const emailField = {
  name: 'email',
  label: 'Email',
  validate: composeValidators(Required, EmailAddress),
};

const passwordField = {
  name: 'password',
  label: 'Password',
  inputType: 'password',
  validate: Required,
};

const confirmPasswordField = {
  name: 'confirmPassword',
  label: 'Confirm password',
  inputType: 'password',
  validate: composeValidators(Required, ConfirmPassword),
};

export default function RegisterForm({ onSubmit, useRecaptcha, recaptchaSiteKey }) {
  let recaptchaChallengeField;
  if (useRecaptcha) {
    const recaptchaField = {
      name: 'recaptchaResponse',
      siteKey: recaptchaSiteKey,
      validate: Required,
    };
    recaptchaChallengeField = <Field component={FormRecaptcha} {...recaptchaField} />;
  }

  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, submitting }) => (
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
      )}
    </Form>
  );
}
