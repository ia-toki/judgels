import { courseAPI } from '../../../../modules/api/jerahmeel/course';
import { getToken } from '../../../../modules/session';

export async function getCourses() {
  const token = getToken();
  return await courseAPI.getCourses(token);
}
