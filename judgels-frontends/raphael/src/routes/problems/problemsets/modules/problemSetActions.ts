import { selectToken } from '../../../../modules/session/sessionSelectors';

export const problemSetActions = {
  getProblemSets: (name?: string, page?: number) => {
    return async (dispatch, getState, { problemSetAPI }) => {
      const token = selectToken(getState());
      return await problemSetAPI.getProblemSets(token, name, page);
    };
  },
};
