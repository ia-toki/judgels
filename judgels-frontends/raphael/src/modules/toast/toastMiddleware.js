import { SubmissionError } from 'redux-form';

import * as toastActions from './toastActions';

export const toastMiddleware: any = store => next => async action => {
  try {
    return await next(action);
  } catch (error) {
    toastActions.showErrorToast(error);

    if (!(error instanceof Error) || error instanceof SubmissionError) {
      throw error;
    }
  }
};
