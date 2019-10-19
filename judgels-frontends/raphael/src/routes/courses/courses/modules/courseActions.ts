import { DelCourse, PutCourse } from './courseReducer';

export const courseActions = {
  getCourses: () => {
    return async (dispatch, getState, { courseAPI }) => {
      return await courseAPI.getCourses();
    };
  },

  getCourseBySlug: (courseSlug: string) => {
    return async (dispatch, getState, { courseAPI }) => {
      const course = await courseAPI.getCourseBySlug(courseSlug);
      dispatch(PutCourse.create(course));
      return course;
    };
  },

  clearCourse: DelCourse.create,
};
