import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutProblemSetProblem, DelProblemSetProblem } from './problemSetProblemReducer';
import { problemSetProblemAPI } from '../../../../../../modules/api/jerahmeel/problemSetProblem';

export function getProblems(problemSetJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblems(token, problemSetJid);
  };
}

export function getProblem(problemSetJid: string, problemAlias: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const problem = await problemSetProblemAPI.getProblem(token, problemSetJid, problemAlias);
    dispatch(PutProblemSetProblem.create(problem));
    return problem;
  };
}

export const clearProblem = DelProblemSetProblem.create;

export function getProblemWorksheet(problemSetJid: string, problemAlias: string, language?: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblemWorksheet(token, problemSetJid, problemAlias, language);
  };
}

export function getProblemStats(problemSetJid: string, problemAlias: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblemStats(token, problemSetJid, problemAlias);
  };
}
