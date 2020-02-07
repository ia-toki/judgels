import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { chapterProblemAPI } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';

export const chapterProblemActions = {
  getProblems: (chapterJid: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return await chapterProblemAPI.getProblems(token, chapterJid);
    };
  },

  getProblemWorksheet: (chapterJid: string, problemAlias: string, language?: string) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return await chapterProblemAPI.getProblemWorksheet(token, chapterJid, problemAlias, language);
    };
  },
};
