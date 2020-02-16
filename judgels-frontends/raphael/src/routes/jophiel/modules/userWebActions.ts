import { selectToken } from '../../../modules/session/sessionSelectors';
import { JophielRole } from '../../../modules/api/jophiel/role';
import { userWebAPI } from '../../../modules/api/jophiel/userWeb';
import { PutWebConfig } from './userWebReducer';

export function getWebConfig() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());

    let config;
    if (token) {
      config = await userWebAPI.getWebConfig(token);
    } else {
      config = { role: JophielRole.Guest };
    }
    dispatch(PutWebConfig.create(config));
  };
}
