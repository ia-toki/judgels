import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { callAction } from '../../../../../modules/callAction';
import { useSession } from '../../../../../modules/session';
import { InfoPanel } from '../../../panels/info/InfoPanel/InfoPanel';

import * as infoActions from '../../../modules/infoActions';
import * as userActions from '../../../modules/userActions';

export default function InfoPage() {
  const { user } = useSession();
  const userJid = user.jid;

  const [state, setState] = useState({
    user: undefined,
    info: undefined,
  });

  const refreshInfo = async () => {
    const [user, info] = await Promise.all([
      callAction(userActions.getUser(userJid)),
      callAction(infoActions.getInfo(userJid)),
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
    await callAction(infoActions.updateInfo(userJid, info));
    await refreshInfo();
  };

  return render();
}
