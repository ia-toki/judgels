import { SubmissionError } from 'redux-form';

import { UserRegistrationData, userAccountAPI } from '../../../../modules/api/jophiel/userAccount';
import { userRegistrationWebAPI } from '../../../../modules/api/jophiel/userRegistration';
import { userSearchAPI } from '../../../../modules/api/jophiel/userSearch';

export function getWebConfig() {
  return async () => {
    return await userRegistrationWebAPI.getWebConfig();
  };
}

export function registerUser(data: UserRegistrationData) {
  return async () => {
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
}
