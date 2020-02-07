import { selectToken } from '../../../../modules/session/sessionSelectors';
import { PutProblemSet, DelProblemSet } from './problemSetReducer';
import { problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';

export const problemSetActions = {
  getProblemSets: (archiveSlug?: string, name?: string, page?: number) => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return await problemSetAPI.getProblemSets(token, archiveSlug, name, page);
    };
  },

  getProblemSetBySlug: (problemSetSlug: string) => {
    return async (dispatch, getState) => {
      const problemSet = await problemSetAPI.getProblemSetBySlug(problemSetSlug);
      dispatch(PutProblemSet.create(problemSet));
      return problemSet;
    };
  },

  clearProblemSet: DelProblemSet.create,
};
