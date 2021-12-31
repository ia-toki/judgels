import { get } from '../http';
import { APP_CONFIG } from '../../../conf';

export const baseCurriculumsURL = `${APP_CONFIG.apiUrls.jerahmeel}/curriculums`;

export const curriculumAPI = {
  getCurriculums: () => {
    return get(`${baseCurriculumsURL}`);
  },
};
