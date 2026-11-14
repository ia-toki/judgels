import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { registerUserMutationOptions } from '../../../../modules/queries/accountRegistration';
import { getRecaptchaSiteKey } from '../../../../modules/webConfig';
import GoogleAuth from '../../login/GoogleAuth/GoogleAuth';
import ResendActivation from '../../needActivation/ResendActivation/ResendActivation';
import RegisterForm from '../RegisterForm/RegisterForm';

import './RegisterPage.scss';

export default function RegisterPage() {
  const [registeredUser, setRegisteredUser] = useState(undefined);
  const [isInternalAuthEnabled, setIsInternalAuthEnabled] = useState(true);

  const registerMutation = useMutation(registerUserMutationOptions());

  const onRegisterUser = async data => {
    const userRegistrationData = {
      username: data.username,
      password: data.password,
      email: data.email,
      name: data.name,
      recaptchaResponse: data.recaptchaResponse,
    };
    await registerMutation.mutateAsync(userRegistrationData);
    setRegisteredUser({
      username: data.username,
      email: data.email,
    });
  };

  const toggleInternalAuth = () => {
    setIsInternalAuthEnabled(prev => !prev);
  };

  let content;
  if (registeredUser) {
    content = (
      <Card title="Activation required" className="register-page__card">
        <p>
          Thank you for registering, <strong>{registeredUser.username}</strong>.
        </p>
        <p data-key="instruction">
          A confirmation email has been sent to&nbsp;
          <strong>{registeredUser.email}</strong> with instruction to activate your account.
        </p>
        <p>Please check your inbox/spam.</p>
        <ResendActivation email={registeredUser.email} />
      </Card>
    );
  } else {
    const recaptchaSiteKey = getRecaptchaSiteKey();
    const registerFormProps = {
      recaptchaEnabled: !!recaptchaSiteKey,
      recaptchaSiteKey,
    };
    content = (
      <Card title="Register" className="register-page__card">
        <GoogleAuth onToggleInternalAuth={toggleInternalAuth} />
        {isInternalAuthEnabled && <RegisterForm onSubmit={onRegisterUser} {...registerFormProps} />}
      </Card>
    );
  }

  return <SingleColumnLayout>{content}</SingleColumnLayout>;
}
