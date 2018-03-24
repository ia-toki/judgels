export const activateActions = {
  activate: (emailCode: string) => {
    return async (dispatch, getState, { userAccountAPI }) => {
      await userAccountAPI.activateUser(emailCode);
    };
  },
};
