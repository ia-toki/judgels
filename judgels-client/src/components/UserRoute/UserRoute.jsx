import { connect } from 'react-redux';
import { Redirect, Route, withRouter } from 'react-router';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

function UserRoute({ component: Component, isLoggedIn, ...rest }) {
  const render = props => (isLoggedIn ? <Component {...props} /> : <Redirect to={{ pathname: '/' }} />);

  return <Route {...rest} render={render} />;
}

const mapStateToProps = state => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default withRouter(connect(mapStateToProps)(UserRoute));
