import { queryOptions } from '@tanstack/react-query';

import { userWebAPI } from '../api/jophiel/userWeb';

export const userWebConfigQueryOptions = token =>
  queryOptions({
    queryKey: ['user-web-config', token],
    queryFn: () => userWebAPI.getWebConfig(token),
  });
