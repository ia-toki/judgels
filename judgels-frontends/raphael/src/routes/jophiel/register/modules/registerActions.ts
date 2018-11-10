import { SubmissionError } from 'redux-form';

import { UserRegistrationData } from 'modules/api/jophiel/userAccount';

export const registerActions = {
  getWebConfig: () => {
    return async (dispatch, getState, { userRegistrationWebAPI }) => {
      return await userRegistrationWebAPI.getWebConfig();
    };
  },

  registerUser: (data: UserRegistrationData) => {
    return async (dispatch, getState, { userSearchAPI, userAccountAPI }) => {
      const usernameExists = await userSearchAPI.usernameExists(data.username);
      const emailExists = await userSearchAPI.emailExists(data.email);

      if (usernameExists || emailExists) {
        const usernameError = usernameExists ? { username: 'Username already exists' } : {};
        const emailError = emailExists ? { email: 'Email already exists' } : {};
        throw new SubmissionError({
          ...usernameError,
          ...emailError,
        });
      }

      await userAccountAPI.registerUser(data);
    };
  },
};
