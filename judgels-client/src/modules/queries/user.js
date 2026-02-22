import { queryOptions } from '@tanstack/react-query';

import { userAPI } from '../api/jophiel/user';
import { getToken } from '../session';

export const userQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid],
    queryFn: () => userAPI.getUser(getToken(), userJid),
  });
