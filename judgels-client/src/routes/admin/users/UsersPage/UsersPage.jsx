import { HTMLTable, Intent } from '@blueprintjs/core';
import { Upload } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { usersQueryOptions } from '../../../../modules/queries/user';

const PAGE_SIZE = 250;

export default function UsersPage() {
  const location = useLocation();
  const page = +(location.search.page || 1);

  const { data: response } = useQuery(usersQueryOptions({ page }));

  const renderAction = () => {
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/users/-/upsert">
        <Upload />
        <span>Upsert users</span>
      </ButtonLink>
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

    const rows = data.page.map(user => (
      <tr key={user.jid}>
        <td>
          <Link to={`/admin/users/${user.username}`}>{user.username}</Link>
        </td>
        <td>{user.email}</td>
        <td>{lastSessionTimesMap[user.jid] ? <FormattedDate value={lastSessionTimesMap[user.jid]} /> : '-'}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Last login</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title="Users" action={renderAction()}>
      {renderUsers()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
