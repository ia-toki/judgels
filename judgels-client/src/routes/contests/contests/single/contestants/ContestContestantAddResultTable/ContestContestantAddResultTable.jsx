import { HTMLTable } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { UserRef } from '../../../../../../components/UserRef/UserRef';

export function ContestContestantAddResultTable({
  usernames,
  insertedContestantProfilesMap,
  alreadyContestantProfilesMap,
}) {
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
    const knownUsernames = [
      ...Object.keys(insertedContestantProfilesMap),
      ...Object.keys(alreadyContestantProfilesMap),
    ];
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
        <h5>Unknown users ({unknownUsernames.length})</h5>
        <HTMLTable striped className="table-list-condensed">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </div>
    );
  };

  return (
    <Flex flexDirection="column" gap={6}>
      {renderContestantsTable('Added contestants', insertedContestantProfilesMap)}
      {renderContestantsTable('Already contestants', alreadyContestantProfilesMap)}
      {renderUnknownContestantsTable()}
    </Flex>
  );
}
