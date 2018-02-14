import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { serviceLogoutActions as injectedServiceLogoutActions } from '../modules/serviceLogoutActions';

interface ServiceLogoutPageProps {
  onLogOut: (redirectUri: string) => Promise<void>;

  match: {
    params: {
      returnUri: string;
    };
  };
}

class ServiceLogoutPage extends React.Component<ServiceLogoutPageProps> {
  async componentDidMount() {
    await this.props.onLogOut(this.props.match.params.returnUri);
  }

  render() {
    return null;
  }
}

export function createServiceLogoutPage(serviceLogoutActions) {
  const mapDispatchToProps = dispatch => ({
    onLogOut: (redirectUri: string) => dispatch(serviceLogoutActions.logOut(redirectUri)),
  });
  return withRouter<any>(connect(undefined, mapDispatchToProps)(ServiceLogoutPage));
}

export default createServiceLogoutPage(injectedServiceLogoutActions);
