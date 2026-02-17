import { QueryClient } from '@tanstack/react-query';

import { router } from '../routes/router';
import { UnauthorizedError } from './api/error';
import { SubmissionError } from './form/submissionError';

import * as toastActions from './toast/toastActions';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      gcTime: 1000 * 60 * 60 * 24, // 24 hours
    },
    mutations: {
      onError: error => {
        if (error instanceof UnauthorizedError) {
          router.navigate({ to: '/logout', replace: true });
          return;
        }
        if (!(error instanceof SubmissionError)) {
          toastActions.showErrorToast(error);
        }
      },
    },
  },
});
