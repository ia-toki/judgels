import { SubmissionError } from '../form/submissionError';

import * as toastActions from './toastActions';

const toastMiddleware = store => next => async action => {
  try {
    return await next(action);
  } catch (error) {
    if (!(error instanceof SubmissionError)) {
      toastActions.showErrorToast(error);
    }
    throw error;
  }
};

export default toastMiddleware;
