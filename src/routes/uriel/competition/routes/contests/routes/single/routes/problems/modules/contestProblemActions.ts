import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';

export const contestProblemActions = {
  fetchMyList: (contestJid: string) => {
    return async (dispatch, getState, { contestProblemAPI }) => {
      const token = selectToken(getState());
      return await contestProblemAPI.getMyProblems(token, contestJid);
    };
  },

  fetchWorksheet: (contestJid: string, problemAlias: string, language: string) => {
    return async (dispatch, getState, { contestProblemAPI }) => {
      const token = selectToken(getState());
      return await contestProblemAPI.getProblemWorksheet(token, contestJid, problemAlias, language);
    };
  },
};
