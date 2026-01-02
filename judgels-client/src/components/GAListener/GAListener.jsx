import { useLocation } from '@tanstack/react-router';
import { useEffect } from 'react';

import { sendGAPageview } from '../../ga';

export function GAListener() {
  const location = useLocation();

  useEffect(() => {
    sendGAPageview(location);
  }, [location]);

  return null;
}
