import { contestWebAPI } from '../../../../../modules/api/uriel/contestWeb';
import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { PutContest } from '../../modules/contestReducer';
import { DelWebConfig, PutWebConfig } from '../../modules/contestWebConfigReducer';

export function getContestBySlugWithWebConfig(contestSlug) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const { contest, config } = await contestWebAPI.getContestBySlugWithWebConfig(token, contestSlug);
    dispatch(PutContest(contest));
    dispatch(PutWebConfig(config));
    return { contest, config };
  };
}

export function getContestByJidWithWebConfig(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const { contest, config } = await contestWebAPI.getContestByJidWithWebConfig(token, contestJid);
    dispatch(PutContest(contest));
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
