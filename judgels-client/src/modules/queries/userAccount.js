import { queryOptions } from '@tanstack/react-query';

import { BadRequestError, NotFoundError } from '../api/error';
import { userAccountAPI } from '../api/jophiel/userAccount';
import { userRegistrationWebAPI } from '../api/jophiel/userRegistration';
import { userSearchAPI } from '../api/jophiel/userSearch';
import { SubmissionError } from '../form/submissionError';

export const registrationWebConfigQueryOptions = () =>
  queryOptions({
    queryKey: ['registration-web-config'],
    queryFn: () => userRegistrationWebAPI.getWebConfig(),
  });

export const registerUserMutationOptions = {
  mutationFn: async data => {
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
  },
};

export const activateUserMutationOptions = {
  mutationFn: emailCode => userAccountAPI.activateUser(emailCode),
};

export const requestResetPasswordMutationOptions = {
  mutationFn: async email => {
    try {
      await userAccountAPI.requestToResetPassword(email);
    } catch (error) {
      if (error instanceof NotFoundError) {
        throw new Error('Email not found.');
      }
      throw error;
    }
  },
};

export const resetPasswordMutationOptions = {
  mutationFn: async ({ emailCode, newPassword }) => {
    try {
      await userAccountAPI.resetPassword({ emailCode, newPassword });
    } catch (error) {
      if (error instanceof BadRequestError) {
        throw new Error('Invalid code.');
      } else {
        throw error;
      }
    }
  },
};
