import { get, post } from '../http';

export const CourseErrors = {
  SlugAlreadyExists: 'Jerahmeel:CourseSlugAlreadyExists',
};

export const baseCoursesURL = `/api/v2/courses`;

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
