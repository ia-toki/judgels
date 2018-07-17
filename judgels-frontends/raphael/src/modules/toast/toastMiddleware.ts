import { toastActions as injectedToastActions } from './toastActions';

export function createToastMiddleware(toastActions) {
  return store => next => async action => {
    try {
      return await next(action);
    } catch (error) {
      toastActions.showErrorToast(error);
      throw error;
    }
  };
}

export const toastMiddleware = createToastMiddleware(injectedToastActions);
