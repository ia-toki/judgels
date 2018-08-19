import { selectToken } from 'modules/session/sessionSelectors';

import { DelWebConfig, PutWebConfig } from '../../modules/contestWebConfigReducer';

export const contestWebActions = {
  getContestBySlugWithWebConfig: (contestJid: string) => {
    return async (dispatch, getState, { contestWebAPI }) => {
      const token = selectToken(getState());
      const contestWithConfig = await contestWebAPI.getContestBySlugWithWebConfig(token, contestJid);
      dispatch(PutWebConfig.create(contestWithConfig.config));
      return contestWithConfig;
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
