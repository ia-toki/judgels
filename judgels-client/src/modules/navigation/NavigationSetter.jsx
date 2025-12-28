import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { setNavigationRef } from './navigationRef';

// Component that captures useNavigate and sets the navigation ref
// This must be rendered inside BrowserRouter
export function NavigationSetter() {
  const navigate = useNavigate();

  useEffect(() => {
    setNavigationRef({
      push: (path, state) => navigate(path, { state }),
      replace: (path, state) => navigate(path, { replace: true, state }),
    });
    return () => setNavigationRef(null);
  }, [navigate]);

  return null;
}
