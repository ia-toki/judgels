import { useMutation } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { activateUserMutationOptions } from '../../../../modules/queries/userAccount';

export default function ActivatePage() {
  const { emailCode } = useParams({ strict: false });
  const navigate = useNavigate();

  const activateMutation = useMutation(activateUserMutationOptions);

  useEffect(() => {
    const activate = async () => {
      await activateMutation.mutateAsync(emailCode);
      navigate({ to: '/registered', search: { source: 'internal' } });
    };
    activate();
  }, [emailCode, navigate]);

  return <SingleColumnLayout></SingleColumnLayout>;
}
