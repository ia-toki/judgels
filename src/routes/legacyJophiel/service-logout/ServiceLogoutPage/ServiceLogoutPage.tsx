import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { serviceLogoutActions as injectedServiceLogoutActions } from '../modules/serviceLogoutActions';

interface ServiceLogoutPageProps extends RouteComponentProps<{ returnUri: string }> {
  onLogOut: (redirectUri: string) => Promise<void>;
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
  const mapDispatchToProps = {
    onLogOut: serviceLogoutActions.logOut,
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(ServiceLogoutPage));
}

export default createServiceLogoutPage(injectedServiceLogoutActions);
