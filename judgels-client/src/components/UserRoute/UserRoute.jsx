import { useSelector } from 'react-redux';
import { Redirect, Route } from 'react-router';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

export default function UserRoute({ component: Component, ...rest }) {
  const isLoggedIn = useSelector(selectIsLoggedIn);

  const render = props => (isLoggedIn ? <Component {...props} /> : <Redirect to={{ pathname: '/' }} />);

  return <Route {...rest} render={render} />;
}
