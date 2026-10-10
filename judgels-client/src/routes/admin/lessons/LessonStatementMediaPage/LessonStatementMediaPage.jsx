import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { lessonStatementAPI } from '../../../../modules/api/lessonStatement';
import { lessonQueryOptions } from '../../../../modules/queries/lesson';
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
  const { lessonJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(lessonQueryOptions(lessonJid));

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
      description={
        <>
          Refer to a media file in a statement as <code>render/&lt;filename&gt;</code>. Previews show a newly uploaded
          file only after it is committed.
        </>
      }
      noun="media"
      files={files}
      canEdit={config.canEdit}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => lessonStatementAPI.downloadStatementMediaFile(getToken(), lessonJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
