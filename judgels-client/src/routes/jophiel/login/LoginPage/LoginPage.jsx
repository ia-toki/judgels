import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { afterLogin, logInMutationOptions } from '../../../../modules/queries/session';
import GoogleAuth from '../../components/GoogleAuth/GoogleAuth';
import LoginForm from '../LoginForm/LoginForm';

import './LoginPage.scss';

export default function LoginPage() {
  const navigate = useNavigate();
  const [isInternalAuthEnabled, setIsInternalAuthEnabled] = useState(true);

  const logInMutation = useMutation(logInMutationOptions);

  const toggleInternalAuth = () => {
    setIsInternalAuthEnabled(prev => !prev);
  };

  const onLogIn = async data => {
    const result = await logInMutation.mutateAsync({
      usernameOrEmail: data.usernameOrEmail,
      password: data.password,
    });
    if (result.redirect) {
      navigate({ to: result.redirect, search: { email: result.email } });
      return;
    }
    await afterLogin(result);
  };

  return (
    <SingleColumnLayout>
      <Card title="Log in" className="card-login">
        <GoogleAuth onToggleInternalAuth={toggleInternalAuth} />
        {isInternalAuthEnabled && <LoginForm onSubmit={onLogIn} />}
      </Card>
    </SingleColumnLayout>
  );
}
