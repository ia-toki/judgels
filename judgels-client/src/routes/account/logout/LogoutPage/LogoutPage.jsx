import { useMutation } from '@tanstack/react-query';
import { Navigate } from '@tanstack/react-router';
import { useEffect } from 'react';

import { logOutMutationOptions } from '../../../../modules/queries/session';

export default function LogoutPage() {
  const logOutMutation = useMutation(logOutMutationOptions);

  useEffect(() => {
    logOutMutation.mutate();
  }, []);

  if (logOutMutation.isError) {
    return <Navigate to="/" />;
  }
  return null;
}
