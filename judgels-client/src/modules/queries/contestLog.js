import { queryOptions } from '@tanstack/react-query';

import { contestLogAPI } from '../api/uriel/contestLog';
import { getToken } from '../session';

export const contestLogsQueryOptions = (contestJid, params) => {
  const { username, problemAlias, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'logs', ...(params ? [params] : [])],
    queryFn: () => contestLogAPI.getLogs(getToken(), contestJid, username, problemAlias, page),
  });
};
