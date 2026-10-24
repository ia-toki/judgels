import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createArchiveMutationOptions } from '../../../../modules/queries/archive';
import ArchiveCreateForm from '../ArchiveCreateForm/ArchiveCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ArchiveCreatePage() {
  const navigate = useNavigate();

  const createArchiveMutation = useMutation(createArchiveMutationOptions);

  const createArchive = async data => {
    await createArchiveMutation.mutateAsync(data, {
      onSuccess: (_data, { slug }) => {
        navigate({ to: `/admin/archives/${slug}` });
        toastActions.showSuccessToast('Archive created.');
      },
    });
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Archives" current="New" />}>
      <ArchiveCreateForm onSubmit={createArchive} onCancel={() => navigate({ to: '/admin/archives' })} />
    </ContentCard>
  );
}
