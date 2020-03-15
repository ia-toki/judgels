import { replace } from 'connected-react-router';

export function redirectToLogout() {
  return async dispatch => {
    dispatch(replace('/logout'));
  };
}
