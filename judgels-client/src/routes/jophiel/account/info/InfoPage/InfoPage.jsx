import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { selectUserJid } from '../../../../../modules/session/sessionSelectors';
import { InfoPanel } from '../../../panels/info/InfoPanel/InfoPanel';

import * as infoActions from '../../../modules/infoActions';
import * as userActions from '../../../modules/userActions';

export default function InfoPage() {
  const userJid = useSelector(selectUserJid);
  const dispatch = useDispatch();

  const [state, setState] = useState({
    user: undefined,
    info: undefined,
  });

  const refreshInfo = async () => {
    const [user, info] = await Promise.all([
      dispatch(userActions.getUser(userJid)),
      dispatch(infoActions.getInfo(userJid)),
    ]);
    setState(prevState => ({ ...prevState, user, info }));
  };

  useEffect(() => {
    refreshInfo();
  }, []);

  const render = () => {
    const { user, info } = state;
    if (!user || !info) {
      return <LoadingState />;
    }
    return <InfoPanel email={user.email} info={info} onUpdateInfo={onUpdateInfo} />;
  };

  const onUpdateInfo = async info => {
    await dispatch(infoActions.updateInfo(userJid, info));
    await refreshInfo();
  };

  return render();
}
