import { SubmissionError } from 'redux-form';

import { toastActions as injectedToastActions } from './toastActions';

export function createToastMiddleware(toastActions) {
  return store => next => async action => {
    try {
      return await next(action);
    } catch (error) {
      toastActions.showErrorToast(error);

      if (!(error instanceof Error) || error instanceof SubmissionError) {
        throw error;
      }
    }
  };
}

export const toastMiddleware: any = createToastMiddleware(injectedToastActions);
