import { queryOptions } from '@tanstack/react-query';

import { NotFoundError } from '../api/error';
import { userSearchAPI } from '../api/jophiel/userSearch';

export const userJidByUsernameQueryOptions = username =>
  queryOptions({
    queryKey: ['user-jid-by-username', username],
    queryFn: async () => {
      const userJidsByUsername = await userSearchAPI.translateUsernamesToJids([username]);
      if (userJidsByUsername[username] === undefined) {
        throw new NotFoundError();
      }
      return userJidsByUsername[username];
    },
  });
