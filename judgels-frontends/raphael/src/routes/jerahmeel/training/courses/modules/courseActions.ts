export const courseActions = {
  getCourses: () => {
    return async (dispatch, getState, { courseAPI }) => {
      return await courseAPI.getCourses();
    };
  },
};
