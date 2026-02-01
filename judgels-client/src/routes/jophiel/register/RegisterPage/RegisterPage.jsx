import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import GoogleAuth from '../../components/GoogleAuth/GoogleAuth';
import ResendActivationEmailButton from '../../components/ResendActivationEmailButton/ResendActivationEmailButton';
import RegisterForm from '../RegisterForm/RegisterForm';

import * as registerActions from '../modules/registerActions';

import './RegisterPage.scss';

export default function RegisterPage() {
  const dispatch = useDispatch();

  const [state, setState] = useState({
    config: undefined,
    registeredUser: undefined,
    isInternalAuthEnabled: true,
  });

  const refreshWebConfig = async () => {
    const config = await dispatch(registerActions.getWebConfig());
    setState(prevState => ({ ...prevState, config }));
  };

  useEffect(() => {
    refreshWebConfig();
  }, []);

  const render = () => {
    const { config } = state;
    if (!config) {
      return null;
    }

    let content;
    if (state.registeredUser) {
      content = (
        <Card title="Activation required" className="card-register">
          <p>
            Thank you for registering, <strong>{state.registeredUser.username}</strong>.
          </p>
          <p data-key="instruction" className="card-register__instruction">
            A confirmation email has been sent to&nbsp;
            <strong>{state.registeredUser.email}</strong> with instruction to activate your account.
          </p>
          <p>Please check your inbox/spam.</p>
          <ResendActivationEmailButton email={state.registeredUser.email} />
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
          {state.isInternalAuthEnabled && <RegisterForm onSubmit={onRegisterUser} {...registerFormProps} />}
        </Card>
      );
    }

    return <SingleColumnLayout>{content}</SingleColumnLayout>;
  };

  const onRegisterUser = async data => {
    const userRegistrationData = {
      username: data.username,
      password: data.password,
      email: data.email,
      name: data.name,
      recaptchaResponse: data.recaptchaResponse,
    };
    await dispatch(registerActions.registerUser(userRegistrationData));
    setState(prevState => ({
      ...prevState,
      registeredUser: {
        username: data.username,
        email: data.email,
      },
    }));
  };

  const toggleInternalAuth = () => {
    setState(prevState => ({ ...prevState, isInternalAuthEnabled: !prevState.isInternalAuthEnabled }));
  };

  return render();
}
