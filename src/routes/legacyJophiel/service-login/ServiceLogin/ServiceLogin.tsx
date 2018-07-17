import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import LoginForm, { LoginFormData } from '../../../jophiel/login/LoginForm/LoginForm';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { Card } from '../../../../components/Card/Card';
import { AppState } from '../../../../modules/store';
import { serviceLoginActions as injectedServiceLoginActions } from '../modules/serviceLoginActions';

interface ServiceLoginProps {
  isLoggedIn: boolean;
  onLogIn: (data: LoginFormData, redirectUri: string, returnUri: string) => Promise<void>;
  onPropagateLogin: (redirectUri: string, returnUri: string) => Promise<void>;

  match: {
    params: {
      redirectUri: string;
      returnUri: string;
    };
  };
}

class ServiceLogin extends React.Component<ServiceLoginProps> {
  async componentDidMount() {
    if (this.props.isLoggedIn) {
      await this.props.onPropagateLogin(this.props.match.params.redirectUri, this.props.match.params.returnUri);
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

export function createServiceLoginContainer(serviceLoginActions) {
  const mapStateToProps = (state: AppState) => ({
    isLoggedIn: state.session.isLoggedIn || false,
  });

  const mapDispatchToProps = dispatch => ({
    onLogIn: (data: LoginFormData, redirectUri: string, returnUri: string) =>
      dispatch(serviceLoginActions.logIn(data.username, data.password, redirectUri, returnUri)),
    onPropagateLogin: (redirectUri: string, returnUri: string) =>
      dispatch(serviceLoginActions.propagateLogin(redirectUri, returnUri)),
  });

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ServiceLogin));
}

const ServiceLoginContainer = createServiceLoginContainer(injectedServiceLoginActions);
export default ServiceLoginContainer;
