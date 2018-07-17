import * as React from 'react';
import { connect } from 'react-redux';

import { logoutActions as injectedLogoutActions } from '../modules/logoutActions';

interface LogoutPageProps {
  onLogOut: () => void;
}

class LogoutPage extends React.Component<LogoutPageProps> {
  componentDidMount() {
    this.props.onLogOut();
  }

  render() {
    return null;
  }
}

export function createLogoutPage(logoutActions) {
  const mapDispatchToProps = {
    onLogOut: () => logoutActions.logOut(window.location.href),
  };
  return connect(undefined, mapDispatchToProps)(LogoutPage);
}

export default createLogoutPage(injectedLogoutActions);
