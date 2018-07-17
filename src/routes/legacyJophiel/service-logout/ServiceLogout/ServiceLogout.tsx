import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { serviceLogoutActions as injectedServiceLogoutActions } from '../modules/serviceLogoutActions';

interface LogoutProps {
  onLogOut: (redirectUri: string) => Promise<void>;

  match: {
    params: {
      returnUri: string;
    };
  };
}

class ServiceLogout extends React.Component<LogoutProps> {
  async componentDidMount() {
    await this.props.onLogOut(this.props.match.params.returnUri);
  }

  render() {
    return null;
  }
}

export function createServiceLogoutContainer(serviceLogoutActions) {
  const mapDispatchToProps = dispatch => ({
    onLogOut: (redirectUri: string) => dispatch(serviceLogoutActions.logOut(redirectUri)),
  });
  return withRouter<any>(connect(undefined, mapDispatchToProps)(ServiceLogout));
}

const ServiceLogoutContainer = createServiceLogoutContainer(injectedServiceLogoutActions);
export default ServiceLogoutContainer;
