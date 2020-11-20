import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { chapterProblemAPI } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';

export function getProblems(chapterJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterProblemAPI.getProblems(token, chapterJid);
  };
}

export function getProblemWorksheet(chapterJid, problemAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterProblemAPI.getProblemWorksheet(token, chapterJid, problemAlias, language);
  };
}
