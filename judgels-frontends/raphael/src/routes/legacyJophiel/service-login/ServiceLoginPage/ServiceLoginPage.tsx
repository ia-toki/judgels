import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { RouteComponentProps } from 'react-router-dom';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { Card } from '../../../../components/Card/Card';
import { AppState } from '../../../../modules/store';

import LoginForm, { LoginFormData } from '../../../jophiel/login/LoginForm/LoginForm';
import * as serviceLoginActions from '../modules/serviceLoginActions';

interface ServiceLoginPageProps extends RouteComponentProps<{ redirectUri: string; returnUri: string }> {
  isLoggedIn: boolean;
  onLogIn: (data: LoginFormData, redirectUri: string, returnUri: string) => void;
  onPropagateLogin: (redirectUri: string, returnUri: string) => void;
}

class ServiceLoginPage extends React.Component<ServiceLoginPageProps> {
  componentDidMount() {
    if (this.props.isLoggedIn) {
      this.props.onPropagateLogin(this.props.match.params.redirectUri, this.props.match.params.returnUri);
    }
  }

  render() {
    const onSubmit = (data: LoginFormData) =>
      this.props.onLogIn(data, this.props.match.params.redirectUri, this.props.match.params.returnUri);

    return (
      <SingleColumnLayout>
        <Card title="Log in" className="card-login">
          <LoginForm onSubmit={onSubmit} />
        </Card>
      </SingleColumnLayout>
    );
  }
}

const mapStateToProps = (state: AppState) => ({
  isLoggedIn: state.session.isLoggedIn || false,
});

const mapDispatchToProps = {
  onLogIn: (data: LoginFormData, redirectUri: string, returnUri: string) =>
    serviceLoginActions.logIn(data.usernameOrEmail, data.password, redirectUri, returnUri),
  onPropagateLogin: serviceLoginActions.propagateLogin,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ServiceLoginPage));
