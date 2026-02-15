import { problemSetProblemAPI } from '../../../../../../modules/api/jerahmeel/problemSetProblem';
import { getToken } from '../../../../../../modules/session';

export async function getProblems(problemSetJid) {
  const token = getToken();
  return await problemSetProblemAPI.getProblems(token, problemSetJid);
}

export async function getProblemWorksheet(problemSetJid, problemAlias, language) {
  const token = getToken();
  return await problemSetProblemAPI.getProblemWorksheet(token, problemSetJid, problemAlias, language);
}

export async function getProblemStats(problemSetJid, problemAlias) {
  const token = getToken();
  return await problemSetProblemAPI.getProblemStats(token, problemSetJid, problemAlias);
}

export async function getProblemReport(problemSetJid, problemAlias) {
  const token = getToken();
  return await problemSetProblemAPI.getProblemReport(token, problemSetJid, problemAlias);
}

export async function getProblemEditorial(problemSetJid, problemAlias, language) {
  return await problemSetProblemAPI.getProblemEditorial(problemSetJid, problemAlias, language);
}
