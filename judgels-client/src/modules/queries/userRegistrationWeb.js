import { queryOptions } from '@tanstack/react-query';

import { userRegistrationWebAPI } from '../api/userRegistrationWeb';

export const userRegistrationWebConfigQueryOptions = () =>
  queryOptions({
    queryKey: ['registration-web-config'],
    queryFn: () => userRegistrationWebAPI.getWebConfig(),
  });
