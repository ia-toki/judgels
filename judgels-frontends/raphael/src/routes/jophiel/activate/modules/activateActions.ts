export const activateActions = {
  activateUser: (emailCode: string) => {
    return async (dispatch, getState, { userAccountAPI }) => {
      await userAccountAPI.activateUser(emailCode);
    };
  },
};
