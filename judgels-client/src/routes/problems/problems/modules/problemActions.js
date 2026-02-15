import { problemAPI } from '../../../../modules/api/jerahmeel/problem';
import { getToken } from '../../../../modules/session';

export async function getProblems(tags, page) {
  const token = getToken();
  return await problemAPI.getProblems(token, tags, page);
}

export async function getProblemTags() {
  return await problemAPI.getProblemTags();
}
