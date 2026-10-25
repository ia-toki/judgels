import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { lessonStatementAPI } from '../../../../modules/api/lessonStatement';
import { lessonBySlugQueryOptions } from '../../../../modules/queries/lesson';
import {
  deleteLessonStatementMediaFileMutationOptions,
  deleteLessonStatementMediaFilesMutationOptions,
  lessonStatementMediaFilesQueryOptions,
  uploadLessonStatementMediaFileMutationOptions,
  uploadLessonStatementMediaZipMutationOptions,
} from '../../../../modules/queries/lessonStatement';
import { getToken } from '../../../../modules/session';
import FilesPanel from '../../catalog/FilesPanel/FilesPanel';

export default function LessonStatementMediaPage() {
  const { lessonSlug } = useParams({ strict: false });

  const {
    data: { jid: lessonJid },
  } = useSuspenseQuery(lessonBySlugQueryOptions(lessonSlug));

  const {
    data: { data: files },
  } = useSuspenseQuery(lessonStatementMediaFilesQueryOptions(lessonJid));

  const uploadFileMutation = useMutation(uploadLessonStatementMediaFileMutationOptions(lessonJid));
  const uploadZipMutation = useMutation(uploadLessonStatementMediaZipMutationOptions(lessonJid));
  const deleteFileMutation = useMutation(deleteLessonStatementMediaFileMutationOptions(lessonJid));
  const deleteFilesMutation = useMutation(deleteLessonStatementMediaFilesMutationOptions(lessonJid));

  return (
    <FilesPanel
      title="Media"
      description="Resources such as images to show in the statement."
      noun="media"
      files={files}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => lessonStatementAPI.downloadStatementMediaFile(getToken(), lessonJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
