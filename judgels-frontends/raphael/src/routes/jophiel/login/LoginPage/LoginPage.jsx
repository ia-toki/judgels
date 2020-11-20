import * as React from 'react';
import { connect } from 'react-redux';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { Card } from '../../../../components/Card/Card';

import LoginForm from '../LoginForm/LoginForm';
import * as loginActions from '../modules/loginActions';

import './LoginPage.css';

function LoginPage({ onLogIn }) {
  return (
    <SingleColumnLayout>
      <Card title="Log in" className="card-login">
        <LoginForm onSubmit={onLogIn} />
      </Card>
    </SingleColumnLayout>
  );
}

const mapDispatchToProps = {
  onLogIn: data => loginActions.logIn(data.usernameOrEmail, data.password),
};

export default connect(undefined, mapDispatchToProps)(LoginPage);
