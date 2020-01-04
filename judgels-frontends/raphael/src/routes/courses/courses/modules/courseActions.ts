import { DelCourse, PutCourse } from './courseReducer';
import { selectToken } from '../../../../modules/session/sessionSelectors';

export const courseActions = {
  getCourses: () => {
    return async (dispatch, getState, { courseAPI }) => {
      const token = selectToken(getState());
      return await courseAPI.getCourses(token);
    };
  },

  getCourseBySlug: (courseSlug: string) => {
    return async (dispatch, getState, { courseAPI }) => {
      const token = selectToken(getState());
      const course = await courseAPI.getCourseBySlug(token, courseSlug);
      dispatch(PutCourse.create(course));
      return course;
    };
  },

  clearCourse: DelCourse.create,
};
