import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, useParams } from 'react-router';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';

import * as activateActions from '../modules/activateActions';

export default function ActivatePage() {
  const { emailCode } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const activate = async () => {
      await dispatch(activateActions.activateUser(emailCode));
      navigate('/registered?source=internal');
    };
    activate();
  }, [emailCode, navigate, dispatch]);

  return <SingleColumnLayout></SingleColumnLayout>;
}
