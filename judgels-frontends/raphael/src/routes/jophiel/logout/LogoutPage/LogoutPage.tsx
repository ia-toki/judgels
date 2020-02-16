import * as React from 'react';
import { connect } from 'react-redux';

import * as logoutActions from '../modules/logoutActions';

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

const mapDispatchToProps = {
  onLogOut: () => logoutActions.logOut(window.location.href),
};
export default connect(undefined, mapDispatchToProps)(LogoutPage);
