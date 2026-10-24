import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { createChapterMutationOptions } from '../../../../modules/queries/chapter';
import ChapterCreateForm from '../ChapterCreateForm/ChapterCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ChapterCreatePage() {
  const navigate = useNavigate();

  const createChapterMutation = useMutation(createChapterMutationOptions);

  const createChapter = async data => {
    await createChapterMutation.mutateAsync(data, {
      onSuccess: chapter => {
        navigate({ to: `/admin/chapters/${chapter.jid}` });
        toastActions.showSuccessToast('Chapter created.');
      },
    });
  };

  return (
    <ContentCard title={<BreadcrumbTitle parent="Chapters" current="New" />}>
      <ChapterCreateForm onSubmit={createChapter} onCancel={() => navigate({ to: '/admin/chapters' })} />
    </ContentCard>
  );
}
