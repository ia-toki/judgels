import { HTMLTable } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { problemWritersQueryOptions } from '../../../../../modules/queries/problemWriter';
import { RolesTopbar } from '../../RolesTopbar/RolesTopbar';

import { RolesActions } from '../../RolesActions/RolesActions';

export default function ProblemWritersPage() {
  const {
    data: { data: writerJids, profilesMap },
  } = useSuspenseQuery(problemWritersQueryOptions());

  const usernameOf = userJid => profilesMap[userJid]?.username ?? userJid;
  const writers = [...writerJids].sort((a, b) => usernameOf(a).localeCompare(usernameOf(b)));

  const renderContent = () => {
    if (writers.length === 0) {
      return (
        <p>
          <small>No writers.</small>
        </p>
      );
    }
    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>User</th>
          </tr>
        </thead>
        <tbody>
          {writers.map(userJid => (
            <tr key={userJid}>
              <td>
                <UserRef profile={profilesMap[userJid]} />
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title="Roles" action={<RolesActions />}>
      <Flex flexDirection="column" gap={5}>
        <RolesTopbar activeItemPath="/admin/roles/problem-writers" />
        {renderContent()}
      </Flex>
    </ContentCard>
  );
}
