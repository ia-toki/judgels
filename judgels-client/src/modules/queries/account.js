import { AccountRegistrationErrors, accountAPI } from '../api/account';
import { BadRequestError, NotFoundError } from '../api/error';
import { SubmissionError } from '../form/submissionError';

function toSubmissionError(error) {
  if (error instanceof BadRequestError) {
    if (error.message === AccountRegistrationErrors.UsernameAlreadyExists) {
      return new SubmissionError({ username: 'Username already exists' });
    }
    if (error.message === AccountRegistrationErrors.EmailAlreadyExists) {
      return new SubmissionError({ email: 'Email already exists' });
    }
  }
  return error;
}

export const registerUserMutationOptions = () => ({
  mutationFn: async data => {
    try {
      await accountAPI.registerUser(data);
    } catch (error) {
      throw toSubmissionError(error);
    }
  },
});

export const activateUserMutationOptions = () => ({
  mutationFn: emailCode => accountAPI.activateUser(emailCode),
});

export const resendActivationEmailMutationOptions = email => ({
  mutationFn: () => accountAPI.resendActivationEmail(email),
});

export const registerGoogleUserMutationOptions = () => ({
  mutationFn: async data => {
    try {
      await accountAPI.registerGoogleUser(data);
    } catch (error) {
      throw toSubmissionError(error);
    }
  },
});

export const requestResetPasswordMutationOptions = () => ({
  mutationFn: async email => {
    try {
      await accountAPI.requestResetPassword(email);
    } catch (error) {
      if (error instanceof NotFoundError) {
        throw new Error('Email not found.');
      }
      throw error;
    }
  },
});

export const resetPasswordMutationOptions = () => ({
  mutationFn: async ({ emailCode, newPassword }) => {
    try {
      await accountAPI.resetPassword({ emailCode, newPassword });
    } catch (error) {
      if (error instanceof BadRequestError) {
        throw new Error('Invalid code.');
      } else {
        throw error;
      }
    }
  },
});
