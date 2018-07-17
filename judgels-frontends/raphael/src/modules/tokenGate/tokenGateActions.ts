import { replace } from 'react-router-redux';

export function createTokenGateActions() {
  return {
    redirectToLogout: async dispatch => {
      dispatch(replace('/logout'));
    },
  };
}

export const tokenGateActions = createTokenGateActions();
