import * as React from 'react';
import { connect } from 'react-redux';

import { logoutActions as injectedLogoutActions } from '../modules/logoutActions';

interface LogoutPageProps {
  onLogOut: () => Promise<void>;
}

class LogoutPage extends React.Component<LogoutPageProps> {
  async componentDidMount() {
    await this.props.onLogOut();
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
