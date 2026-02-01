import { useEffect } from 'react';
import { useDispatch } from 'react-redux';

import * as logoutActions from '../modules/logoutActions';

export default function LogoutPage() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(logoutActions.logOut(window.location.href));
  }, []);

  const render = () => {
    return null;
  };

  return render();
}
