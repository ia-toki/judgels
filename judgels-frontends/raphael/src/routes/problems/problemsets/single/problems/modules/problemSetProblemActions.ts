import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutProblemSetProblem, DelProblemSetProblem } from './problemSetProblemReducer';

export const problemSetProblemActions = {
  getProblems: (problemSetJid: string) => {
    return async (dispatch, getState, { problemSetProblemAPI }) => {
      const token = selectToken(getState());
      return await problemSetProblemAPI.getProblems(token, problemSetJid);
    };
  },

  getProblem: (problemSetJid: string, problemAlias: string) => {
    return async (dispatch, getState, { problemSetProblemAPI }) => {
      const token = selectToken(getState());
      const problem = await problemSetProblemAPI.getProblem(token, problemSetJid, problemAlias);
      dispatch(PutProblemSetProblem.create(problem));
      return problem;
    };
  },

  clearProblem: DelProblemSetProblem.create,

  getProblemWorksheet: (problemSetJid: string, problemAlias: string, language?: string) => {
    return async (dispatch, getState, { problemSetProblemAPI }) => {
      const token = selectToken(getState());
      return await problemSetProblemAPI.getProblemWorksheet(token, problemSetJid, problemAlias, language);
    };
  },

  getProblemStats: (problemSetJid: string, problemAlias: string) => {
    return async (dispatch, getState, { problemSetProblemAPI }) => {
      const token = selectToken(getState());
      return await problemSetProblemAPI.getProblemStats(token, problemSetJid, problemAlias);
    };
  },
};
