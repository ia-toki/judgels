import { useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { callAction } from '../../../../modules/callAction';

import * as activateActions from '../modules/activateActions';

export default function ActivatePage() {
  const { emailCode } = useParams({ strict: false });
  const navigate = useNavigate();

  useEffect(() => {
    const activate = async () => {
      await callAction(activateActions.activateUser(emailCode));
      navigate({ to: '/registered', search: { source: 'internal' } });
    };
    activate();
  }, [emailCode, navigate]);

  return <SingleColumnLayout></SingleColumnLayout>;
}
