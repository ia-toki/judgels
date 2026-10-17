import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import { Field, Form } from 'react-final-form';

import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { FormRecaptcha } from '../../../../components/forms/FormRecaptcha/FormRecaptcha';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import {
  ConfirmPassword,
  EmailAddress,
  Required,
  Username,
  composeValidators,
} from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

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

          <Flex>
            <Button type="submit" text="Register" intent={Intent.PRIMARY} loading={submitting} />
            <p className="form-register__actions-login">
              Have an account already? <Link to="/login">Log in now</Link>
            </p>
          </Flex>
        </form>
      )}
    </Form>
  );
}
