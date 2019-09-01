import { replace } from 'connected-react-router';

export function createTokenGateActions() {
  return {
    redirectToLogout: async dispatch => {
      dispatch(replace('/logout'));
    },
  };
}

export const tokenGateActions = createTokenGateActions();
