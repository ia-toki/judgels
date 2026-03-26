import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { usersQueryOptions } from '../../../../modules/queries/user';
import { UserUpsertDialog } from '../UserUpsertDialog/UserUpsertDialog';
import { UsersTable } from '../UsersTable/UsersTable';

const PAGE_SIZE = 250;

export default function UsersPage() {
  const location = useLocation();
  const page = +(location.search.page || 1);

  const { data: response } = useQuery(usersQueryOptions({ page }));

  const renderAction = () => {
    return (
      <ActionButtons>
        <UserUpsertDialog />
      </ActionButtons>
    );
  };

  const renderUsers = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data, lastSessionTimesMap } = response;
    if (data.page.length === 0) {
      return (
        <p>
          <small>No users.</small>
        </p>
      );
    }

    return <UsersTable users={data.page} lastSessionTimesMap={lastSessionTimesMap} />;
  };

  return (
    <ContentCard title="Users">
      {renderAction()}
      {renderUsers()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
