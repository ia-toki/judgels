import * as React from 'react';
import { connect } from 'react-redux';

import { logoutActions as injectedLogoutActions } from '../modules/logoutActions';

interface LogoutProps {
  onLogOut: () => Promise<void>;
}

class Logout extends React.Component<LogoutProps> {
  async componentDidMount() {
    await this.props.onLogOut();
  }

  render() {
    return null;
  }
}

export function createLogoutContainer(logoutActions) {
  const mapDispatchToProps = dispatch => ({
    onLogOut: () => dispatch(logoutActions.logOut(window.location.href)),
  });
  return connect(undefined, mapDispatchToProps)(Logout);
}

const LogoutContainer = createLogoutContainer(injectedLogoutActions);
export default LogoutContainer;
