import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ArchiveErrors, archiveAPI } from '../api/jerahmeel/archive';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const archivesQueryOptions = () =>
  queryOptions({
    queryKey: ['archives'],
    queryFn: () => archiveAPI.getArchives(getToken()),
  });

export const archiveBySlugQueryOptions = archiveSlug =>
  queryOptions({
    queryKey: ['archive-by-slug', archiveSlug],
    queryFn: async () => {
      const response = await archiveAPI.getArchives(getToken());
      return response.data.find(a => a.slug === archiveSlug);
    },
  });

export const createArchiveMutationOptions = {
  mutationFn: async data => {
    try {
      await archiveAPI.createArchive(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(archivesQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['archive-by-slug'] });
  },
};

export const updateArchiveMutationOptions = archiveJid => ({
  mutationFn: async data => {
    try {
      await archiveAPI.updateArchive(getToken(), archiveJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(archivesQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['archive-by-slug'] });
  },
});
