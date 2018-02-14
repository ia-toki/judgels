import * as React from 'react';
import { connect } from 'react-redux';

import LoginForm, { LoginFormData } from '../LoginForm/LoginForm';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { Card } from '../../../../components/Card/Card';
import { loginActions as injectedLoginActions } from '../modules/loginActions';

export interface LoginPageProps {
  onLogIn: (data: LoginFormData) => Promise<void>;
}

const LoginPage = (props: LoginPageProps) => (
  <SingleColumnLayout>
    <Card title="Log in" className="card-login">
      <LoginForm onSubmit={props.onLogIn} />
    </Card>
  </SingleColumnLayout>
);

export function createLoginPage(loginActions) {
  const mapDispatchToProps = dispatch => ({
    onLogIn: (data: LoginFormData) => dispatch(loginActions.logIn(window.location.href, data.username, data.password)),
  });

  return connect(undefined, mapDispatchToProps)(LoginPage);
}

export default createLoginPage(injectedLoginActions);
