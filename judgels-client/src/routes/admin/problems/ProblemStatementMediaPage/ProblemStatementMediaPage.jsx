import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemStatementAPI } from '../../../../modules/api/problemStatement';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
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
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

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
      description="Resources such as images to show in the statement."
      noun="media"
      files={files}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => problemStatementAPI.downloadStatementMediaFile(getToken(), problemJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
