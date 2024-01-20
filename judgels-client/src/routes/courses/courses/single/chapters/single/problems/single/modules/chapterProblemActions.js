import { chapterProblemAPI } from '../../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { selectToken } from '../../../../../../../../../modules/session/sessionSelectors';
import { RefreshChapterProblem } from './chapterProblemReducer';

export function getProblemWorksheet(chapterJid, problemAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterProblemAPI.getProblemWorksheet(token, chapterJid, problemAlias, language);
  };
}

export function refreshProblem({ shouldScrollToEditorial }) {
  return async dispatch => {
    await dispatch(RefreshChapterProblem({ refreshKey: Date.now(), shouldScrollToEditorial }));
  };
}
