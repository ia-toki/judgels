import { UnauthorizedError } from './api/error';
import { SubmissionError } from './form/submissionError';
import { getNavigationRef } from './navigation/navigationRef';

import * as toastActions from './toast/toastActions';

export async function callAction(promise) {
  try {
    return await promise;
  } catch (error) {
    if (error instanceof UnauthorizedError) {
      getNavigationRef().replace('/logout');
      return;
    }
    if (!(error instanceof SubmissionError)) {
      toastActions.showErrorToast(error);
    }
    throw error;
  }
}
