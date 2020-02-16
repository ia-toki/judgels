import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import * as serviceLogoutActions from '../modules/serviceLogoutActions';

interface ServiceLogoutPageProps extends RouteComponentProps<{ returnUri: string }> {
  onLogOut: (redirectUri: string) => void;
}

class ServiceLogoutPage extends React.Component<ServiceLogoutPageProps> {
  componentDidMount() {
    this.props.onLogOut(this.props.match.params.returnUri);
  }

  render() {
    return null;
  }
}

const mapDispatchToProps = {
  onLogOut: serviceLogoutActions.logOut,
};
export default withRouter(connect(undefined, mapDispatchToProps)(ServiceLogoutPage));
