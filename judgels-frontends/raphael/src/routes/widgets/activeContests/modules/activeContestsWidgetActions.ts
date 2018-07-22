import { selectToken } from '../../../../modules/session/sessionSelectors';

export const activeContestWidgetActions = {
  getActiveContests: () => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getActiveContests(token);
    };
  },
};
