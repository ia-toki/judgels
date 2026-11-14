import { AccountRegistrationErrors, accountRegistrationAPI } from '../api/accountRegistration';
import { BadRequestError } from '../api/error';
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

export const registerUserMutationOptions = {
  mutationFn: async data => {
    try {
      await accountRegistrationAPI.registerUser(data);
    } catch (error) {
      throw toSubmissionError(error);
    }
  },
};

export const activateUserMutationOptions = {
  mutationFn: emailCode => accountRegistrationAPI.activateUser(emailCode),
};

export const registerGoogleUserMutationOptions = {
  mutationFn: async data => {
    try {
      await accountRegistrationAPI.registerGoogleUser(data);
    } catch (error) {
      throw toSubmissionError(error);
    }
  },
};
