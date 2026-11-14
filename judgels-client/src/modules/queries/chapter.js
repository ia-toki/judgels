import { queryOptions } from '@tanstack/react-query';

import { chapterAPI } from '../api/chapter';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chaptersQueryOptions = () =>
  queryOptions({
    queryKey: ['chapters'],
    queryFn: () => chapterAPI.getChapters(getToken()),
  });

export const chapterByJidQueryOptions = chapterJid =>
  queryOptions({
    ...chaptersQueryOptions(),
    select: response => response.data.find(c => c.jid === chapterJid),
  });

export const createChapterMutationOptions = {
  mutationFn: data => chapterAPI.createChapter(getToken(), data),
  onSuccess: () => {
    queryClient.invalidateQueries(chaptersQueryOptions());
  },
};

export const updateChapterMutationOptions = chapterJid => ({
  mutationFn: data => chapterAPI.updateChapter(getToken(), chapterJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(chaptersQueryOptions());
  },
});
