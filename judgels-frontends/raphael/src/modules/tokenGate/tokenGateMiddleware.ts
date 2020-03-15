import { UnauthorizedError } from '../../modules/api/error';

import * as tokenGateActions from './tokenGateActions';

export const tokenGateMiddleware: any = store => next => async action => {
  try {
    return await next(action);
  } catch (error) {
    // redirects to /logout if receiving UnauthorizedError
    if (error instanceof UnauthorizedError) {
      return await tokenGateActions.redirectToLogout();
    }

    throw error;
  }
};
