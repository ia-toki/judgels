import { useEffect } from 'react';

import { callAction } from '../../../../modules/callAction';

import * as logoutActions from '../modules/logoutActions';

export default function LogoutPage() {
  useEffect(() => {
    callAction(logoutActions.logOut(window.location.href));
  }, []);

  const render = () => {
    return null;
  };

  return render();
}
