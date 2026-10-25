import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemEditorialAPI } from '../../../../modules/api/problemEditorial';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import {
  deleteProblemEditorialMediaFileMutationOptions,
  deleteProblemEditorialMediaFilesMutationOptions,
  problemEditorialMediaFilesQueryOptions,
  uploadProblemEditorialMediaFileMutationOptions,
  uploadProblemEditorialMediaZipMutationOptions,
} from '../../../../modules/queries/problemEditorial';
import { getToken } from '../../../../modules/session';
import FilesPanel from '../../catalog/FilesPanel/FilesPanel';

export default function ProblemEditorialMediaPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { data: files },
  } = useSuspenseQuery(problemEditorialMediaFilesQueryOptions(problemJid));

  const uploadFileMutation = useMutation(uploadProblemEditorialMediaFileMutationOptions(problemJid));
  const uploadZipMutation = useMutation(uploadProblemEditorialMediaZipMutationOptions(problemJid));
  const deleteFileMutation = useMutation(deleteProblemEditorialMediaFileMutationOptions(problemJid));
  const deleteFilesMutation = useMutation(deleteProblemEditorialMediaFilesMutationOptions(problemJid));

  return (
    <FilesPanel
      title="Media"
      description="Resources such as images to show in the editorial."
      noun="media"
      files={files}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => problemEditorialAPI.downloadEditorialMediaFile(getToken(), problemJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
