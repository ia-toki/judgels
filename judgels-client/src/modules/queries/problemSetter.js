import { queryOptions } from '@tanstack/react-query';

import { problemSetterAPI } from '../api/problemSetter';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSettersQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'setters'],
    queryFn: () => problemSetterAPI.getSetters(getToken(), problemJid),
  });

export const setProblemSettersMutationOptions = problemJid => ({
  mutationFn: setterUsernamesMap => problemSetterAPI.setSetters(getToken(), problemJid, setterUsernamesMap),
  onSuccess: () => {
    queryClient.invalidateQueries(problemSettersQueryOptions(problemJid));
  },
});
