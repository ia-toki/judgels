import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const chapterProblemActions = {
  getProblems: (chapterJid: string) => {
    return async (dispatch, getState, { chapterProblemAPI }) => {
      const token = selectToken(getState());
      return await chapterProblemAPI.getProblems(token, chapterJid);
    };
  },

  getProblemWorksheet: (chapterJid: string, problemAlias: string, language?: string) => {
    return async (dispatch, getState, { chapterProblemAPI }) => {
      const token = selectToken(getState());
      return await chapterProblemAPI.getProblemWorksheet(token, chapterJid, problemAlias, language);
    };
  },
};
