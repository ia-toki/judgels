import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemEditorialAPI } from '../../../../modules/api/problemEditorial';
import { problemQueryOptions } from '../../../../modules/queries/problem';
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
  const { problemJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

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
      description={
        <>
          Refer to a media file in an editorial as <code>render/&lt;filename&gt;</code>. Previews show a newly uploaded
          file only after it is committed.
        </>
      }
      noun="media"
      files={files}
      canEdit={config.canEdit}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => problemEditorialAPI.downloadEditorialMediaFile(getToken(), problemJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
