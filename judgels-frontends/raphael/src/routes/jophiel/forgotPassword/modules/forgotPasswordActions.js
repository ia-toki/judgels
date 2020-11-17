import { NotFoundError } from '../../../../modules/api/error';
import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';

export function requestToResetPassword(email: string) {
  return async () => {
    try {
      await userAccountAPI.requestToResetPassword(email);
    } catch (error) {
      if (error instanceof NotFoundError) {
        throw new Error('Email not found.');
      }
      throw error;
    }
  };
}
