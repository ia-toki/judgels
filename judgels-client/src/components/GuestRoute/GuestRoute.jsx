import { useSelector } from 'react-redux';
import { Redirect, Route } from 'react-router';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

export default function GuestRoute({ component: Component, ...rest }) {
  const isLoggedIn = useSelector(selectIsLoggedIn);

  const render = props => (isLoggedIn ? <Redirect to={{ pathname: '/' }} /> : <Component {...props} />);

  return <Route {...rest} render={render} />;
}
