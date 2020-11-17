import { selectToken } from '../../../../../modules/session/sessionSelectors';

import { contestWebAPI } from '../../../../../modules/api/uriel/contestWeb';
import { PutContest } from '../../modules/contestReducer';
import { DelWebConfig, PutWebConfig } from '../../modules/contestWebConfigReducer';

export function getContestBySlugWithWebConfig(contestSlug: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const { contest, config } = await contestWebAPI.getContestBySlugWithWebConfig(token, contestSlug);
    dispatch(PutContest.create(contest));
    dispatch(PutWebConfig.create(config));
    return { contest, config };
  };
}

export function getContestByJidWithWebConfig(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const { contest, config } = await contestWebAPI.getContestByJidWithWebConfig(token, contestJid);
    dispatch(PutContest.create(contest));
    dispatch(PutWebConfig.create(config));
    return { contest, config };
  };
}

export function getWebConfig(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const config = await contestWebAPI.getWebConfig(token, contestJid);
    dispatch(PutWebConfig.create(config));
  };
}

export const clearWebConfig = DelWebConfig.create;
