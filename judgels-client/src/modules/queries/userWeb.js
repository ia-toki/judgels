import { queryOptions } from '@tanstack/react-query';

import { userWebAPI } from '../api/jophiel/userWeb';
import { getToken } from '../session';

export const userWebConfigQueryOptions = () =>
  queryOptions({
    queryKey: ['user-web-config'],
    queryFn: () => userWebAPI.getWebConfig(getToken()),
  });
