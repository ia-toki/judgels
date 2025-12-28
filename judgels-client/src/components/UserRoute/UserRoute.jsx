import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

import { selectIsLoggedIn } from '../../modules/session/sessionSelectors';

export default function UserRoute({ children }) {
  const isLoggedIn = useSelector(selectIsLoggedIn);

  if (!isLoggedIn) {
    return <Navigate to="/" replace />;
  }

  return children;
}
