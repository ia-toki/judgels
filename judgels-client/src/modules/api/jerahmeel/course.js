import { get, post } from '../http';
import { APP_CONFIG } from '../../../conf';

export const CourseErrors = {
  SlugAlreadyExists: 'Jerahmeel:CourseSlugAlreadyExists',
};

export const baseCoursesURL = `${APP_CONFIG.apiUrl}/courses`;

export function baseCourseURL(courseJid) {
  return `${baseCoursesURL}/${courseJid}`;
}

export const courseAPI = {
  createCourse: (token, data) => {
    return post(baseCoursesURL, token, data);
  },

  updateCourse: (token, courseJid, data) => {
    return post(`${baseCourseURL(courseJid)}`, token, data);
  },

  getCourses: token => {
    return get(`${baseCoursesURL}`, token);
  },

  getCourseBySlug: (token, courseSlug) => {
    return get(`${baseCoursesURL}/slug/${courseSlug}`, token);
  },
};
