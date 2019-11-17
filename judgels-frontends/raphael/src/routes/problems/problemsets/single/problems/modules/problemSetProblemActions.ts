import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export const problemSetProblemActions = {
  getProblems: (problemSetJid: string) => {
    return async (dispatch, getState, { problemSetProblemAPI }) => {
      const token = selectToken(getState());
      return await problemSetProblemAPI.getProblems(token, problemSetJid);
    };
  },
};
