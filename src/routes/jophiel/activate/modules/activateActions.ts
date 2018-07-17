export const activateActions = {
  activate: (emailCode: string) => {
    return async (dispatch, getState, { userAPI }) => {
      await userAPI.activateUser(emailCode);
    };
  },
};
