export const courseActions = {
  getCourse: () => {
    return async (dispatch, getState, { courseAPI }) => {
      return await courseAPI.getCourse();
    };
  },
};
