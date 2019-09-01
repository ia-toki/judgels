import { selectToken } from '../../../modules/session/sessionSelectors';
import { JophielRole } from '../../../modules/api/jophiel/role';

import { PutWebConfig } from './userWebReducer';

export const userWebActions = {
  getWebConfig: () => {
    return async (dispatch, getState, { userWebAPI }) => {
      const token = selectToken(getState());

      let config;
      if (token) {
        config = await userWebAPI.getWebConfig(token);
      } else {
        config = { role: JophielRole.Guest };
      }
      dispatch(PutWebConfig.create(config));
    };
  },
};
