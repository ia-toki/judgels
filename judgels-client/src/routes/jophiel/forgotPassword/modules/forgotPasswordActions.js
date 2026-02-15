import { NotFoundError } from '../../../../modules/api/error';
import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';

export async function requestToResetPassword(email) {
  try {
    await userAccountAPI.requestToResetPassword(email);
  } catch (error) {
    if (error instanceof NotFoundError) {
      throw new Error('Email not found.');
    }
    throw error;
  }
}
