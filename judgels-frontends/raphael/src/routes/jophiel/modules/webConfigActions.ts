import { PutWebConfig } from './webConfigReducer';

export const webConfigActions = {
  get: () => {
    return async (dispatch, getState, { webAPI }) => {
      const webConfig = await webAPI.getConfig();
      dispatch(PutWebConfig.create(webConfig));
    };
  },
};
