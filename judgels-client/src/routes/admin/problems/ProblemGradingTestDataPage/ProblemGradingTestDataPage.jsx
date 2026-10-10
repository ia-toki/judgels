import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemGradingAPI } from '../../../../modules/api/problemGrading';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import {
  deleteProblemGradingTestDataFileMutationOptions,
  deleteProblemGradingTestDataFilesMutationOptions,
  problemGradingTestDataFilesQueryOptions,
  uploadProblemGradingTestDataFileMutationOptions,
  uploadProblemGradingTestDataZipMutationOptions,
} from '../../../../modules/queries/problemGrading';
import { getToken } from '../../../../modules/session';
import FilesPanel from '../../catalog/FilesPanel/FilesPanel';

export default function ProblemGradingTestDataPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { data: files },
  } = useSuspenseQuery(problemGradingTestDataFilesQueryOptions(problemJid));

  const uploadFileMutation = useMutation(uploadProblemGradingTestDataFileMutationOptions(problemJid));
  const uploadZipMutation = useMutation(uploadProblemGradingTestDataZipMutationOptions(problemJid));
  const deleteFileMutation = useMutation(deleteProblemGradingTestDataFileMutationOptions(problemJid));
  const deleteFilesMutation = useMutation(deleteProblemGradingTestDataFilesMutationOptions(problemJid));

  return (
    <FilesPanel
      title="Test data"
      description="The input and output files that the grading config assigns to test cases."
      noun="test data"
      files={files}
      canEdit={config.canEdit}
      onUploadFile={uploadFileMutation.mutateAsync}
      onUploadZip={uploadZipMutation.mutateAsync}
      onDownloadFile={filename => problemGradingAPI.downloadGradingTestDataFile(getToken(), problemJid, filename)}
      onDeleteFile={deleteFileMutation.mutateAsync}
      onDeleteFiles={deleteFilesMutation.mutateAsync}
    />
  );
}
