import { Navigate } from '@tanstack/react-router';

export default function EnabledRoute({ enabled, children }) {
  if (!enabled) {
    return <Navigate to="/" />;
  }

  return children;
}
