import { contestWebAPI } from '../../../../../modules/api/uriel/contestWeb';
import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { DelWebConfig, PutWebConfig } from '../../modules/contestWebConfigReducer';

export function getContestByJidWithWebConfig(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const { contest, config } = await contestWebAPI.getContestByJidWithWebConfig(token, contestJid);
    dispatch(PutWebConfig(config));
    return { contest, config };
  };
}

export function getWebConfig(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const config = await contestWebAPI.getWebConfig(token, contestJid);
    dispatch(PutWebConfig(config));
  };
}

export const clearWebConfig = DelWebConfig;
