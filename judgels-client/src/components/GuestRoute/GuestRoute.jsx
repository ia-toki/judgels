import { Navigate } from '@tanstack/react-router';
import { useSelector } from 'react-redux';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

export default function GuestRoute({ children }) {
  const isLoggedIn = useSelector(selectIsLoggedIn);

  if (isLoggedIn) {
    return <Navigate to="/" />;
  }

  return children;
}
