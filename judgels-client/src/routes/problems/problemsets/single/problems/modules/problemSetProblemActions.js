import { problemSetProblemAPI } from '../../../../../../modules/api/jerahmeel/problemSetProblem';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export function getProblems(problemSetJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblems(token, problemSetJid);
  };
}

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

export function getProblemReport(problemSetJid, problemAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetProblemAPI.getProblemReport(token, problemSetJid, problemAlias);
  };
}

export function getProblemEditorial(problemSetJid, problemAlias, language) {
  return async () => {
    return await problemSetProblemAPI.getProblemEditorial(problemSetJid, problemAlias, language);
  };
}
