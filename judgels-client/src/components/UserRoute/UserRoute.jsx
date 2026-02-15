import { Navigate } from '@tanstack/react-router';

import { useSession } from '../../modules/session';

export default function UserRoute({ children }) {
  const { isLoggedIn } = useSession();

  if (!isLoggedIn) {
    return <Navigate to="/" />;
  }

  return children;
}
