import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemGradingAPI } from '../../../../modules/api/problemGrading';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import {
  deleteProblemGradingHelperFileMutationOptions,
  deleteProblemGradingHelperFilesMutationOptions,
  problemGradingHelperFilesQueryOptions,
  uploadProblemGradingHelperFileMutationOptions,
  uploadProblemGradingHelperZipMutationOptions,
} from '../../../../modules/queries/problemGrading';
import { getToken } from '../../../../modules/session';
import FilesPanel from '../../catalog/FilesPanel/FilesPanel';

export default function ProblemGradingHelpersPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { data: files },
  } = useSuspenseQuery(problemGradingHelperFilesQueryOptions(problemJid));

  const uploadFileMutation = useMutation(uploadProblemGradingHelperFileMutationOptions(problemJid));
  const uploadZipMutation = useMutation(uploadProblemGradingHelperZipMutationOptions(problemJid));
  const deleteFileMutation = useMutation(deleteProblemGradingHelperFileMutationOptions(problemJid));
  const deleteFilesMutation = useMutation(deleteProblemGradingHelperFilesMutationOptions(problemJid));

  return (
    <FilesPanel
      title="Helpers"
      description="The source files that the grading config uses as a custom scorer or a communicator."
      noun="helper"
      files={files}
      canEdit={config.canEdit}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => problemGradingAPI.downloadGradingHelperFile(getToken(), problemJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
