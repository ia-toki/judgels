import { useEffect, useState } from 'react';

export function useShowAfterDelay(delayMs) {
  const [show, setShow] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setShow(true), delayMs);
    return () => clearTimeout(timer);
  }, [delayMs]);

  return show;
}
