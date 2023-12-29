import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import GoogleAuth from '../../components/GoogleAuth/GoogleAuth';
import LoginForm from '../LoginForm/LoginForm';

import * as loginActions from '../modules/loginActions';

import './LoginPage.scss';

class LoginPage extends Component {
  state = {
    isInternalAuthEnabled: true,
  };

  render() {
    return (
      <SingleColumnLayout>
        <Card title="Log in" className="card-login">
          <GoogleAuth onToggleInternalAuth={this.toggleInternalAuth} />
          {this.state.isInternalAuthEnabled && <LoginForm onSubmit={this.props.onLogIn} />}
        </Card>
      </SingleColumnLayout>
    );
  }

  toggleInternalAuth = () => {
    this.setState(prevState => ({ isInternalAuthEnabled: !prevState.isInternalAuthEnabled }));
  };
}

const mapDispatchToProps = {
  onLogIn: data => loginActions.logIn(data.usernameOrEmail, data.password),
};

export default connect(undefined, mapDispatchToProps)(LoginPage);
