import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { callAction } from '../../../../modules/callAction';
import GoogleAuth from '../../components/GoogleAuth/GoogleAuth';
import LoginForm from '../LoginForm/LoginForm';

import * as loginActions from '../modules/loginActions';

import './LoginPage.scss';

export default function LoginPage() {
  const [state, setState] = useState({
    isInternalAuthEnabled: true,
  });

  const render = () => {
    return (
      <SingleColumnLayout>
        <Card title="Log in" className="card-login">
          <GoogleAuth onToggleInternalAuth={toggleInternalAuth} />
          {state.isInternalAuthEnabled && <LoginForm onSubmit={onLogIn} />}
        </Card>
      </SingleColumnLayout>
    );
  };

  const toggleInternalAuth = () => {
    setState(prevState => ({ ...prevState, isInternalAuthEnabled: !prevState.isInternalAuthEnabled }));
  };

  const onLogIn = data => callAction(loginActions.logIn(data.usernameOrEmail, data.password));

  return render();
}
