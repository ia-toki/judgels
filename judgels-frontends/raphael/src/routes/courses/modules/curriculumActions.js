import { curriculumAPI } from '../../../modules/api/jerahmeel/curriculum';

export function getCurriculum() {
  return async () => {
    const response = await curriculumAPI.getCurriculums();
    return response.data[0];
  };
}
