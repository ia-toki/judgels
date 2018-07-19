import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { DelWebConfig, PutWebConfig } from '../../../modules/contestWebConfigReducer';

export const contestWebConfigActions = {
  getWebConfig: (contestJid: string) => {
    return async (dispatch, getState, { contestWebAPI }) => {
      const token = selectToken(getState());
      const config = await contestWebAPI.getWebConfig(token, contestJid);
      dispatch(PutWebConfig.create(config));
    };
  },

  clearConfig: DelWebConfig.create,
};
