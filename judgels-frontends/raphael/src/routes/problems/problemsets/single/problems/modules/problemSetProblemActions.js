import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutProblemSetProblem, DelProblemSetProblem } from './problemSetProblemReducer';
import { problemSetProblemAPI } from '../../../../../../modules/api/jerahmeel/problemSetProblem';

export function getProblems(problemSetJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblems(token, problemSetJid);
  };
}

export function getProblem(problemSetJid, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const problem = await problemSetProblemAPI.getProblem(token, problemSetJid, problemAlias);
    dispatch(PutProblemSetProblem(problem));
    return problem;
  };
}

export const clearProblem = DelProblemSetProblem;

export function getProblemWorksheet(problemSetJid, problemAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblemWorksheet(token, problemSetJid, problemAlias, language);
  };
}

export function getProblemStats(problemSetJid, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblemStats(token, problemSetJid, problemAlias);
  };
}
