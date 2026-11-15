import { Alert, Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { UserRole, isSuperadmin } from '../../../../../modules/api/userRole';
import { setUserRolesMutationOptions, userRolesQueryOptions } from '../../../../../modules/queries/userRole';
import { RolesTopbar } from '../../RolesTopbar/RolesTopbar';

import * as toastActions from '../../../../../modules/toast/toastActions';
import { RolesActions } from '../../RolesActions/RolesActions';

export default function ProblemWritersPage() {
  const {
    data: { data, profilesMap },
  } = useSuspenseQuery(userRolesQueryOptions());

  const [writerToRemove, setWriterToRemove] = useState(undefined);
  const removeWriterMutation = useMutation(setUserRolesMutationOptions(writerToRemove?.userJid));

  const writers = data
    .filter(({ roles }) => roles.includes(UserRole.ProblemWriter) && !isSuperadmin(roles))
    .map(({ userJid, roles }) => ({
      userJid,
      roles,
      profile: profilesMap[userJid],
      username: profilesMap[userJid]?.username ?? userJid,
    }))
    .sort((a, b) => a.username.localeCompare(b.username));

  const removeWriter = async () => {
    await removeWriterMutation.mutateAsync(writerToRemove.roles.filter(role => role !== UserRole.ProblemWriter));
    toastActions.showSuccessToast('Writer removed.');
    setWriterToRemove(undefined);
  };

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
            <th />
          </tr>
        </thead>
        <tbody>
          {writers.map(writer => (
            <tr key={writer.userJid}>
              <td>{writer.profile ? <UserRef profile={writer.profile} /> : writer.username}</td>
              <td className="col-fit">
                <Button
                  small
                  minimal
                  intent={Intent.DANGER}
                  icon={<Trash />}
                  aria-label={`Remove ${writer.username}`}
                  onClick={() => setWriterToRemove(writer)}
                />
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

      <Alert
        isOpen={writerToRemove !== undefined}
        intent={Intent.DANGER}
        icon={<Trash />}
        confirmButtonText="Remove"
        cancelButtonText="Cancel"
        loading={removeWriterMutation.isPending}
        onConfirm={removeWriter}
        onCancel={() => setWriterToRemove(undefined)}
      >
        {writerToRemove && `Remove ${writerToRemove.username} from problem writers?`}
      </Alert>
    </ContentCard>
  );
}
