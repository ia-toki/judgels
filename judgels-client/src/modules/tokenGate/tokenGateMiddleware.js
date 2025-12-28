import { UnauthorizedError } from '../api/error';
import { getNavigationRef } from '../navigation/navigationRef';

const tokenGateMiddleware = store => next => async action => {
  try {
    return await next(action);
  } catch (error) {
    // redirects to /logout if receiving UnauthorizedError
    if (error instanceof UnauthorizedError) {
      getNavigationRef().replace('/logout');
      return;
    }

    throw error;
  }
};

export default tokenGateMiddleware;
