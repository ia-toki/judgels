import { NotFoundError } from '../../../../modules/api/error';

export const forgotPasswordActions = {
  requestToReset: (email: string) => {
    return async (dispatch, getState, { userAccountAPI }) => {
      try {
        await userAccountAPI.requestToResetPassword(email);
      } catch (error) {
        if (error instanceof NotFoundError) {
          throw new Error('Email not found.');
        }
        throw error;
      }
    };
  },
};
