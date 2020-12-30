import { Component } from 'react';
import { connect } from 'react-redux';

import * as logoutActions from '../modules/logoutActions';

class LogoutPage extends Component {
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
