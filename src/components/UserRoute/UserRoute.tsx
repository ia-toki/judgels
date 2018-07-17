import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect, Route, withRouter } from 'react-router';

import { AppState } from '../../modules/store';

const UserRoute = ({ component: Component, isLoggedIn, ...rest }) => {
  const render = props => (isLoggedIn ? <Component {...props} /> : <Redirect to={{ pathname: '/login' }} />);

  return <Route {...rest} render={render} />;
};

const mapStateToProps = (state: AppState) => ({
  isLoggedIn: state.session.isLoggedIn || false,
});

export default withRouter<any>(connect(mapStateToProps)(UserRoute));
