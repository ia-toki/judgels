import { selectToken } from '../../../../modules/session/sessionSelectors';
import { PutProblemSet, DelProblemSet } from './problemSetReducer';
import { problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';

export function getProblemSets(archiveSlug?: string, name?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetAPI.getProblemSets(token, archiveSlug, name, page);
  };
}

export function getProblemSetBySlug(problemSetSlug: string) {
  return async (dispatch, getState) => {
    const problemSet = await problemSetAPI.getProblemSetBySlug(problemSetSlug);
    dispatch(PutProblemSet.create(problemSet));
    return problemSet;
  };
}

export const clearProblemSet = DelProblemSet.create;
