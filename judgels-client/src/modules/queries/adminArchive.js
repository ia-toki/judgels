import { queryOptions } from '@tanstack/react-query';

import { adminArchiveAPI } from '../api/admin/archive';
import { ArchiveErrors } from '../api/archive';
import { BadRequestError } from '../api/error';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const archivesQueryOptions = () =>
  queryOptions({
    queryKey: ['admin', 'archives'],
    queryFn: () => adminArchiveAPI.getArchives(getToken()),
  });

export const archiveBySlugQueryOptions = archiveSlug =>
  queryOptions({
    queryKey: ['admin', 'archive-by-slug', archiveSlug],
    queryFn: async () => {
      const response = await adminArchiveAPI.getArchives(getToken());
      return response.data.find(a => a.slug === archiveSlug);
    },
  });

export const createArchiveMutationOptions = {
  mutationFn: async data => {
    try {
      await adminArchiveAPI.createArchive(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(archivesQueryOptions());
  },
};

export const updateArchiveMutationOptions = archiveJid => ({
  mutationFn: async data => {
    try {
      await adminArchiveAPI.updateArchive(getToken(), archiveJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(archivesQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['admin', 'archive-by-slug'] });
  },
});
