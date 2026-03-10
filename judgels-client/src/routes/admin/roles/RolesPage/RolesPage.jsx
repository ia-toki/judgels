import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { userRolesQueryOptions } from '../../../../modules/queries/userRole';
import { RoleEditDialog } from '../RoleEditDialog/RoleEditDialog';

export default function RolesPage() {
  const { data: response } = useQuery(userRolesQueryOptions());

  const renderRoles = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap } = response;
    if (data.length === 0) {
      return (
        <p>
          <small>No roles.</small>
        </p>
      );
    }

    const rows = data.map(entry => {
      const profile = profilesMap[entry.userJid];
      const username = profile ? profile.username : entry.userJid;
      const { role } = entry;

      return (
        <tr key={entry.userJid}>
          <td>{username}</td>
          <td>{role.jophiel || '-'}</td>
          <td>{role.sandalphon || '-'}</td>
          <td>{role.uriel || '-'}</td>
          <td>{role.jerahmeel || '-'}</td>
        </tr>
      );
    });

    return (
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>User</th>
            <th>Account</th>
            <th>Problem/Lesson</th>
            <th>Contest</th>
            <th>Training</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard>
      <h3>Roles</h3>
      <hr />
      <RoleEditDialog currentData={response} />
      {renderRoles()}
    </ContentCard>
  );
}
