import { queryOptions } from '@tanstack/react-query';

import { contestEditorialAPI } from '../api/uriel/contestEditorial';

export const contestEditorialQueryOptions = (contestJid, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'editorial', ...(params ? [params] : [])],
    queryFn: () => contestEditorialAPI.getEditorial(contestJid, language),
  });
};
