import { useMutation } from '@tanstack/react-query';
import { useEffect } from 'react';

import { logOutMutationOptions } from '../../../../modules/queries/session';

export default function LogoutPage() {
  const logOutMutation = useMutation(logOutMutationOptions);

  useEffect(() => {
    logOutMutation.mutate();
  }, []);

  return null;
}
