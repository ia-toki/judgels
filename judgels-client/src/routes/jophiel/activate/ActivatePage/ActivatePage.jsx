import { useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';

import * as activateActions from '../modules/activateActions';

export default function ActivatePage() {
  const { emailCode } = useParams({ strict: false });
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const activate = async () => {
      await dispatch(activateActions.activateUser(emailCode));
      navigate({ to: '/registered', search: { source: 'internal' } });
    };
    activate();
  }, [emailCode, navigate, dispatch]);

  return <SingleColumnLayout></SingleColumnLayout>;
}
