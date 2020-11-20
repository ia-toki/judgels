import { replace } from 'connected-react-router';

import { UnauthorizedError } from '../api/error';

const tokenGateMiddleware = store => next => async action => {
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

export default tokenGateMiddleware;
