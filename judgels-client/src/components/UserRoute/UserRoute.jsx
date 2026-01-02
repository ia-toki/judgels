import { Navigate } from '@tanstack/react-router';
import { useSelector } from 'react-redux';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

export default function UserRoute({ children }) {
  const isLoggedIn = useSelector(selectIsLoggedIn);

  if (!isLoggedIn) {
    return <Navigate to="/" />;
  }

  return children;
}
