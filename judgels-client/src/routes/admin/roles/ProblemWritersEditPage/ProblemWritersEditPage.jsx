import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { UsernamesEditForm, parseUsernames } from '../../../../components/forms/UsernamesEditForm/UsernamesEditForm';
import {
  problemWritersQueryOptions,
  setProblemWritersMutationOptions,
} from '../../../../modules/queries/problemWriter';

import * as toastActions from '../../../../modules/toast/toastActions';

const MAX_WRITERS = 5000;

function validateUsernames(text) {
  if (parseUsernames(text).length > MAX_WRITERS) {
    return `At most ${MAX_WRITERS} writers are allowed`;
  }
  return undefined;
}

export default function ProblemWritersEditPage() {
  const navigate = useNavigate();

  const {
    data: { data: writerJids, profilesMap },
  } = useSuspenseQuery(problemWritersQueryOptions());

  const setWritersMutation = useMutation(setProblemWritersMutationOptions());

  const usernames = writerJids
    .map(userJid => profilesMap[userJid]?.username ?? userJid)
    .sort((a, b) => a.localeCompare(b));

  const goToWriters = () => navigate({ to: '/admin/roles/problem-writers', ignoreBlocker: true });

  const setWriters = async data => {
    await setWritersMutation.mutateAsync(parseUsernames(data.usernames).map(username => ({ username })));
    toastActions.showSuccessToast('Writers updated.');
    goToWriters();
  };

  return (
    <ContentCard
      title={
        <BreadcrumbTitle
          items={[
            { text: 'Roles', to: '/admin/roles' },
            { text: 'Problem Writers', to: '/admin/roles/problem-writers' },
            { text: 'Edit' },
          ]}
        />
      }
    >
      <UsernamesEditForm
        label="Writers"
        initialValues={{ usernames: usernames.join('\n') }}
        validateUsernames={validateUsernames}
        onSubmit={setWriters}
        onCancel={goToWriters}
      />
    </ContentCard>
  );
}
