import * as React from 'react';
import { connect } from 'react-redux';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { Card } from '../../../../components/Card/Card';

import LoginForm, { LoginFormData } from '../LoginForm/LoginForm';
import * as loginActions from '../modules/loginActions';

import './LoginPage.css';

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

const mapDispatchToProps = {
  onLogIn: (data: LoginFormData) => loginActions.logIn(window.location.href, data.usernameOrEmail, data.password),
};

export default connect(undefined, mapDispatchToProps)(LoginPage);
