import { useMutation } from '@tanstack/react-query';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { uploadContestFileMutationOptions } from '../../../../../../modules/queries/contestFile';
import ContestFileUploadForm from '../ContestFileUploadForm/ContestFileUploadForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestFileUploadCard({ contest }) {
  const uploadFileMutation = useMutation(uploadContestFileMutationOptions(contest.jid));

  const uploadFile = async data => {
    await uploadFileMutation.mutateAsync(data.file, {
      onSuccess: () => {
        toastActions.showSuccessToast('File uploaded.');
      },
    });
  };

  return (
    <ContentCard>
      <ContestFileUploadForm onSubmit={uploadFile} />
    </ContentCard>
  );
}
