import { HTMLTable } from '@blueprintjs/core';

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
      <>
        <h5>
          {title} ({usernames.length})
        </h5>
        <HTMLTable striped className="table-list-condensed contest-contestant-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
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
      <>
        <h5>Not contestants ({unknownUsernames.length})</h5>
        <HTMLTable striped className="table-list-condensed contest-contestant-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };

  return (
    <>
      {renderContestantsTable('Removed contestants', deletedContestantProfilesMap)}
      {renderUnknownContestantsTable()}
    </>
  );
}
