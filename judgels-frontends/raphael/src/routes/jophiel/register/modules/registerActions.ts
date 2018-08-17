import { SubmissionError } from 'redux-form';

import { UserRegistrationData } from 'modules/api/jophiel/userAccount';

export const registerActions = {
  getWebConfig: () => {
    return async (dispatch, getState, { userRegistrationWebAPI }) => {
      return await userRegistrationWebAPI.getConfig();
    };
  },

  registerUser: (userRegistrationData: UserRegistrationData) => {
    return async (dispatch, getState, { userAPI, userAccountAPI }) => {
      const usernameExists = await userAPI.usernameExists(userRegistrationData.username);
      const emailExists = await userAPI.emailExists(userRegistrationData.email);

      if (usernameExists || emailExists) {
        const usernameError = usernameExists ? { username: 'Username already exists' } : {};
        const emailError = emailExists ? { email: 'Email already exists' } : {};
        throw new SubmissionError({
          ...usernameError,
          ...emailError,
        });
      }

      await userAccountAPI.registerUser(userRegistrationData);
    };
  },
};
