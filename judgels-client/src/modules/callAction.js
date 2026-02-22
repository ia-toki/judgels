import { SubmissionError } from './form/submissionError';

import * as toastActions from './toast/toastActions';

export async function callAction(promise) {
  try {
    return await promise;
  } catch (error) {
    if (!(error instanceof SubmissionError)) {
      toastActions.showErrorToast(error);
    }
    throw error;
  }
}
