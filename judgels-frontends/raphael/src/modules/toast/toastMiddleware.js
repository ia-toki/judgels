import { SubmissionError } from 'redux-form';

import * as toastActions from './toastActions';

const toastMiddleware = store => next => async action => {
  try {
    return await next(action);
  } catch (error) {
    toastActions.showErrorToast(error);

    if (!(error instanceof Error) || error instanceof SubmissionError) {
      throw error;
    }
  }
};

export default toastMiddleware;
