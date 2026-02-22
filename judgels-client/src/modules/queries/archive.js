import { queryOptions } from '@tanstack/react-query';

import { archiveAPI } from '../api/jerahmeel/archive';
import { getToken } from '../session';

export const archivesQueryOptions = () =>
  queryOptions({
    queryKey: ['archives'],
    queryFn: () => archiveAPI.getArchives(getToken()),
  });
