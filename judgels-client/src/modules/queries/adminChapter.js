import { queryOptions } from '@tanstack/react-query';

import { adminChapterAPI } from '../api/admin/chapter';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chaptersQueryOptions = () =>
  queryOptions({
    queryKey: ['admin', 'chapters'],
    queryFn: () => adminChapterAPI.getChapters(getToken()),
  });

export const chapterByJidQueryOptions = chapterJid =>
  queryOptions({
    queryKey: ['admin', 'chapter-by-jid', chapterJid],
    queryFn: async () => {
      const response = await adminChapterAPI.getChapters(getToken());
      return response.data.find(c => c.jid === chapterJid);
    },
  });

export const createChapterMutationOptions = {
  mutationFn: data => adminChapterAPI.createChapter(getToken(), data),
  onSuccess: () => {
    queryClient.invalidateQueries(chaptersQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['admin', 'chapter-by-jid'] });
  },
};

export const updateChapterMutationOptions = chapterJid => ({
  mutationFn: data => adminChapterAPI.updateChapter(getToken(), chapterJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(chaptersQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['admin', 'chapter-by-jid'] });
  },
});
