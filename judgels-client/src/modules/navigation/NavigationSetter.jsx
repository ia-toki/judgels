import { useNavigate } from '@tanstack/react-router';
import { useEffect } from 'react';

import { setNavigationRef } from './navigationRef';

// Component that captures useNavigate and sets the navigation ref
// This must be rendered inside RouterProvider
export function NavigationSetter() {
  const navigate = useNavigate();

  useEffect(() => {
    setNavigationRef({
      push: (path, state) => navigate({ to: path, state }),
      replace: (path, state) => navigate({ to: path, replace: true, state }),
    });
    return () => setNavigationRef(null);
  }, [navigate]);

  return null;
}
