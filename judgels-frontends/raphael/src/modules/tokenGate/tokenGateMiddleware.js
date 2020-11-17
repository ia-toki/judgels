import { replace } from 'connected-react-router';

import { UnauthorizedError } from '../../modules/api/error';

export const tokenGateMiddleware: any = store => next => async action => {
  try {
    return await next(action);
  } catch (error) {
    // redirects to /logout if receiving UnauthorizedError
    if (error instanceof UnauthorizedError) {
      return await next(replace('/logout'));
    }

    throw error;
  }
};
