import { useMutation, useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import {
  registerUserMutationOptions,
  registrationWebConfigQueryOptions,
} from '../../../../modules/queries/userAccount';
import GoogleAuth from '../../components/GoogleAuth/GoogleAuth';
import ResendActivationEmailButton from '../../components/ResendActivationEmailButton/ResendActivationEmailButton';
import RegisterForm from '../RegisterForm/RegisterForm';

import './RegisterPage.scss';

export default function RegisterPage() {
  const [registeredUser, setRegisteredUser] = useState(undefined);
  const [isInternalAuthEnabled, setIsInternalAuthEnabled] = useState(true);

  const { data: config } = useQuery(registrationWebConfigQueryOptions());

  const registerMutation = useMutation(registerUserMutationOptions);

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

  if (!config) {
    return null;
  }

  let content;
  if (registeredUser) {
    content = (
      <Card title="Activation required" className="card-register">
        <p>
          Thank you for registering, <strong>{registeredUser.username}</strong>.
        </p>
        <p data-key="instruction" className="card-register__instruction">
          A confirmation email has been sent to&nbsp;
          <strong>{registeredUser.email}</strong> with instruction to activate your account.
        </p>
        <p>Please check your inbox/spam.</p>
        <ResendActivationEmailButton email={registeredUser.email} />
      </Card>
    );
  } else {
    const registerFormProps = {
      useRecaptcha: config.useRecaptcha,
      recaptchaSiteKey: config.recaptcha && config.recaptcha.siteKey,
    };
    content = (
      <Card title="Register and start training for free" className="card-register">
        <GoogleAuth onToggleInternalAuth={toggleInternalAuth} />
        {isInternalAuthEnabled && <RegisterForm onSubmit={onRegisterUser} {...registerFormProps} />}
      </Card>
    );
  }

  return <SingleColumnLayout>{content}</SingleColumnLayout>;
}
