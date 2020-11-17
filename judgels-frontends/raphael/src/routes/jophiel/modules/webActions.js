import { webAPI } from '../../../modules/api/jophiel/web';
import { PutWebConfig } from './webReducer';

export function getConfig() {
  return async dispatch => {
    const config = await webAPI.getConfig();
    dispatch(PutWebConfig.create(config));
  };
}
