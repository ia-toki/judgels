export const courseActions = {
  getCourses: () => {
    return async (dispatch, getState, { courseAPI }) => {
      return await courseAPI.getCourses();
    };
  },
  getCourseById: (courseId: number) => {
    return async (dispatch, getState, { courseAPI }) => {
      return await courseAPI.getCourseById(courseId);
    };
  },
};
