import { Navigate } from '@tanstack/react-router';

import { useSession } from '../../modules/session';

export default function GuestRoute({ children }) {
  const { isLoggedIn } = useSession();

  if (isLoggedIn) {
    return <Navigate to="/" />;
  }

  return children;
}
