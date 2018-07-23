import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect, Route, withRouter } from 'react-router';

import { AppState } from '../../modules/store';
import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

const UserRoute = ({ component: Component, isLoggedIn, ...rest }) => {
  const render = props => (isLoggedIn ? <Component {...props} /> : <Redirect to={{ pathname: '/' }} />);

  return <Route {...rest} render={render} />;
};

const mapStateToProps = (state: AppState) => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default withRouter<any>(connect(mapStateToProps)(UserRoute));
