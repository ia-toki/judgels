import { accountPasswordAPI } from '../api/accountPassword';
import { BadRequestError, NotFoundError } from '../api/error';

export const requestToResetPasswordMutationOptions = () => ({
  mutationFn: async email => {
    try {
      await accountPasswordAPI.requestToResetPassword(email);
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
      await accountPasswordAPI.resetPassword({ emailCode, newPassword });
    } catch (error) {
      if (error instanceof BadRequestError) {
        throw new Error('Invalid code.');
      } else {
        throw error;
      }
    }
  },
});
