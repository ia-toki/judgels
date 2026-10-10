import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemStatementAPI } from '../../../../modules/api/problemStatement';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  deleteProblemStatementMediaFileMutationOptions,
  deleteProblemStatementMediaFilesMutationOptions,
  problemStatementMediaFilesQueryOptions,
  uploadProblemStatementMediaFileMutationOptions,
  uploadProblemStatementMediaZipMutationOptions,
} from '../../../../modules/queries/problemStatement';
import { getToken } from '../../../../modules/session';
import FilesPanel from '../../catalog/FilesPanel/FilesPanel';

export default function ProblemStatementMediaPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { data: files },
  } = useSuspenseQuery(problemStatementMediaFilesQueryOptions(problemJid));

  const uploadFileMutation = useMutation(uploadProblemStatementMediaFileMutationOptions(problemJid));
  const uploadZipMutation = useMutation(uploadProblemStatementMediaZipMutationOptions(problemJid));
  const deleteFileMutation = useMutation(deleteProblemStatementMediaFileMutationOptions(problemJid));
  const deleteFilesMutation = useMutation(deleteProblemStatementMediaFilesMutationOptions(problemJid));

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
      onDownloadFile={filename => problemStatementAPI.downloadStatementMediaFile(getToken(), problemJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
