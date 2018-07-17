import { NotFoundError } from '../../../../modules/api/error';

export const forgotPasswordActions = {
  requestToReset: (email: string) => {
    return async (dispatch, getState, { userAPI }) => {
      try {
        await userAPI.requestToResetUserPassword(email);
      } catch (error) {
        if (error instanceof NotFoundError) {
          throw new Error('Email not found.');
        }
        throw error;
      }
    };
  },
};
