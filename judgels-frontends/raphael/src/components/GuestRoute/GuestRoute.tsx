import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect, Route, withRouter } from 'react-router';

import { AppState } from '../../modules/store';
import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

const GuestRoute = ({ component: Component, isLoggedIn, ...rest }) => {
  const render = props => (isLoggedIn ? <Redirect to={{ pathname: '/' }} /> : <Component {...props} />);

  return <Route {...rest} render={render} />;
};

const mapStateToProps = (state: AppState) => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default withRouter<any>(connect(mapStateToProps)(GuestRoute));
