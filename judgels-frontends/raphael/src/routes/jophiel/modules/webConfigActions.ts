import { PutWebConfig } from './webConfigReducer';

export const webConfigActions = {
  getWebConfig: () => {
    return async (dispatch, getState, { webAPI }) => {
      const webConfig = await webAPI.getWebConfig();
      dispatch(PutWebConfig.create(webConfig));
    };
  },
};
