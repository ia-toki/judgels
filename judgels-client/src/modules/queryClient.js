import { QueryClient } from '@tanstack/react-query';

import { SubmissionError } from './form/submissionError';

import * as toastActions from './toast/toastActions';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 3 * 1000, // 3 seconds
      gcTime: 1000 * 60 * 60 * 24, // 24 hours
    },
    mutations: {
      onError: error => {
        if (!(error instanceof SubmissionError)) {
          toastActions.showErrorToast(error);
        }
      },
    },
  },
});
