export const userAccountActions = {
  resendActivationEmail: (email: string) => {
    return async (dispatch, getState, { userAccountAPI, toastActions }) => {
      await userAccountAPI.resendActivationEmail(email);
      toastActions.showToast(`Email has been sent`);
    };
  },
};
