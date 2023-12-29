import { userWebAPI } from '../../../modules/api/jophiel/userWeb';
import { selectToken } from '../../../modules/session/sessionSelectors';
import { PutWebConfig } from './userWebReducer';

export function getWebConfig() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const config = await userWebAPI.getWebConfig(token);
    dispatch(PutWebConfig(config));
  };
}
