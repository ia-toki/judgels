import { SubmissionError } from 'redux-form';

import { UserRegistrationData } from '../../../../modules/api/jophiel/user';

export const registerActions = {
  register: (userRegistrationData: UserRegistrationData) => {
    return async (dispatch, getState, { userAPI }) => {
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

      await userAPI.registerUser(userRegistrationData);
    };
  },
};
