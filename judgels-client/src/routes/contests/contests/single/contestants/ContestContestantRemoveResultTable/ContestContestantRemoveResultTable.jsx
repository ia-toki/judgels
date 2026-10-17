import { HTMLTable } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { UserRef } from '../../../../../../components/UserRef/UserRef';

export function ContestContestantRemoveResultTable({ usernames, deletedContestantProfilesMap }) {
  const renderContestantsTable = (title, profilesMap) => {
    const usernames = Object.keys(profilesMap)
      .slice()
      .sort((u1, u2) => u1.localeCompare(u2));

    if (usernames.length === 0) {
      return null;
    }

    const rows = usernames.map(username => (
      <tr key={username}>
        <td>
          <UserRef profile={profilesMap[username]} />
        </td>
      </tr>
    ));

    return (
      <div>
        <h5>
          {title} ({usernames.length})
        </h5>
        <HTMLTable striped className="table-list-condensed">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderUnknownContestantsTable = () => {
    const knownUsernames = Object.keys(deletedContestantProfilesMap);
    const unknownUsernames = usernames
      .filter(u => knownUsernames.indexOf(u) === -1)
      .slice()
      .sort((u1, u2) => u1.localeCompare(u2));

    if (unknownUsernames.length === 0) {
      return null;
    }

    const rows = unknownUsernames.map(username => (
      <tr key={username}>
        <td>{username}</td>
      </tr>
    ));

    return (
      <div>
        <h5>Not contestants ({unknownUsernames.length})</h5>
        <HTMLTable striped className="table-list-condensed">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </div>
    );
  };

  return (
    <Flex flexDirection="column" gap={6}>
      {renderContestantsTable('Removed contestants', deletedContestantProfilesMap)}
      {renderUnknownContestantsTable()}
    </Flex>
  );
}
