import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useHistory, useParams } from 'react-router-dom';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';

import * as activateActions from '../modules/activateActions';

export default function ActivatePage() {
  const { emailCode } = useParams();
  const history = useHistory();
  const dispatch = useDispatch();

  useEffect(() => {
    const activate = async () => {
      await dispatch(activateActions.activateUser(emailCode));
      history.push('/registered?source=internal');
    };
    activate();
  }, [emailCode, history, dispatch]);

  return <SingleColumnLayout></SingleColumnLayout>;
}
