import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

import { sendGAPageview } from '../../ga';

export function GAListener() {
  const location = useLocation();

  useEffect(() => {
    sendGAPageview(location);
  }, [location]);

  return null;
}
