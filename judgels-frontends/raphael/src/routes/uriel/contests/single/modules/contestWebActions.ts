import { selectToken } from 'modules/session/sessionSelectors';

import { PutContest } from '../../modules/contestReducer';
import { DelWebConfig, PutWebConfig } from '../../modules/contestWebConfigReducer';

export const contestWebActions = {
  getContestBySlugWithWebConfig: (contestJid: string) => {
    return async (dispatch, getState, { contestWebAPI }) => {
      const token = selectToken(getState());
      const { contest, config } = await contestWebAPI.getContestBySlugWithWebConfig(token, contestJid);
      dispatch(PutContest.create(contest));
      dispatch(PutWebConfig.create(config));
      return { contest, config };
    };
  },

  getWebConfig: (contestJid: string) => {
    return async (dispatch, getState, { contestWebAPI }) => {
      const token = selectToken(getState());
      const config = await contestWebAPI.getWebConfig(token, contestJid);
      dispatch(PutWebConfig.create(config));
    };
  },

  clearWebConfig: DelWebConfig.create,
};
