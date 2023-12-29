import { problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { DelProblemSet, PutProblemSet } from './problemSetReducer';

export function getProblemSets(archiveSlug, name, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetAPI.getProblemSets(token, archiveSlug, name, page);
  };
}

export function getProblemSetBySlug(problemSetSlug) {
  return async (dispatch, getState) => {
    const problemSet = await problemSetAPI.getProblemSetBySlug(problemSetSlug);
    dispatch(PutProblemSet(problemSet));
    return problemSet;
  };
}

export const clearProblemSet = DelProblemSet;
