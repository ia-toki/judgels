import { useState } from 'react';
import { useDispatch } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import GoogleAuth from '../../components/GoogleAuth/GoogleAuth';
import LoginForm from '../LoginForm/LoginForm';

import * as loginActions from '../modules/loginActions';

import './LoginPage.scss';

export default function LoginPage() {
  const dispatch = useDispatch();

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

  const onLogIn = data => dispatch(loginActions.logIn(data.usernameOrEmail, data.password));

  return render();
}
