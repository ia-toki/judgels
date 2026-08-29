import { APP_CONFIG } from '../../conf';
import { get, post } from './http';

export const baseCurriculumsURL = `${APP_CONFIG.apiUrl}/curriculums`;

export function baseCurriculumURL(curriculumJid) {
  return `${baseCurriculumsURL}/${curriculumJid}`;
}

export const curriculumAPI = {
  updateCurriculum: (token, curriculumJid, data) => {
    return post(`${baseCurriculumURL(curriculumJid)}`, token, data);
  },

  getCurriculums: token => {
    return get(baseCurriculumsURL, token);
  },
};
