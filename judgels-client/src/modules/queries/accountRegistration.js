import { accountRegistrationAPI } from '../api/accountRegistration';
import { userSearchAPI } from '../api/userSearch';
import { SubmissionError } from '../form/submissionError';

export const registerUserMutationOptions = {
  mutationFn: async data => {
    const usernameExists = await userSearchAPI.hasUsername(data.username);
    const emailExists = await userSearchAPI.hasEmail(data.email);

    if (usernameExists || emailExists) {
      const usernameError = usernameExists ? { username: 'Username already exists' } : {};
      const emailError = emailExists ? { email: 'Email already exists' } : {};
      throw new SubmissionError({
        ...usernameError,
        ...emailError,
      });
    }

    await accountRegistrationAPI.registerUser(data);
  },
};

export const activateUserMutationOptions = {
  mutationFn: emailCode => accountRegistrationAPI.activateUser(emailCode),
};

export const registerGoogleUserMutationOptions = {
  mutationFn: async data => {
    const usernameExists = await userSearchAPI.hasUsername(data.username);
    if (usernameExists) {
      throw new SubmissionError({ username: 'Username already exists' });
    }

    await accountRegistrationAPI.registerGoogleUser(data);
  },
};
