import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/courses`;

export const adminCourseAPI = {
  getCourses: token => {
    return get(baseURL, token);
  },

  getCourseBySlug: (token, slug) => {
    return get(`${baseURL}/slug/${slug}`, token);
  },

  createCourse: (token, data) => {
    return post(baseURL, token, data);
  },

  updateCourse: (token, courseJid, data) => {
    return post(`${baseURL}/${courseJid}`, token, data);
  },
};
