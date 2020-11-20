import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect, Route, withRouter } from 'react-router';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

function GuestRoute({ component: Component, isLoggedIn, ...rest }) {
  const render = props => (isLoggedIn ? <Redirect to={{ pathname: '/' }} /> : <Component {...props} />);

  return <Route {...rest} render={render} />;
}

const mapStateToProps = state => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default withRouter(connect(mapStateToProps)(GuestRoute));
