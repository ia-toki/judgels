import { queryOptions } from '@tanstack/react-query';

import { submissionProgrammingAPI } from '../api/jerahmeel/submissionProgramming';
import { getToken } from '../session';

export const submissionWithSourceQueryOptions = (submissionId, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['submissions', submissionId, 'source', ...(params ? [params] : [])],
    queryFn: () => submissionProgrammingAPI.getSubmissionWithSource(getToken(), submissionId, language),
  });
};
