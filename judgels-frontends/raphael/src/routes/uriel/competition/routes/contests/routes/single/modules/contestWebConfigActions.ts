import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { DelWebConfig, PutWebConfig } from '../../../modules/contestWebConfigReducer';

export const contestWebConfigActions = {
  fetch: (contestJid: string) => {
    return async (dispatch, getState, { contestWebAPI }) => {
      const token = selectToken(getState());
      const config = await contestWebAPI.getConfig(token, contestJid);
      dispatch(PutWebConfig.create(config));
    };
  },

  clear: DelWebConfig.create,
};
