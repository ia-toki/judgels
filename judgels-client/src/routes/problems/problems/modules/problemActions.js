import { trainingProblemAPI } from '../../../../modules/api/trainingProblem';
import { trainingProblemTagAPI } from '../../../../modules/api/trainingProblemTag';
import { getToken } from '../../../../modules/session';

export async function getProblems(tags, page) {
  const token = getToken();
  return await trainingProblemAPI.getProblems(token, tags, page);
}

export async function getProblemTags() {
  return await trainingProblemTagAPI.getProblemTags();
}
