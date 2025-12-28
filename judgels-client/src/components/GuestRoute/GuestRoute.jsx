import { useSelector } from 'react-redux';
import { Navigate } from 'react-router';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

export default function GuestRoute({ children }) {
  const isLoggedIn = useSelector(selectIsLoggedIn);

  if (isLoggedIn) {
    return <Navigate to="/" replace />;
  }

  return children;
}
