import { HTMLTable } from '@blueprintjs/core';

import { UserRef } from '../../../../../../components/UserRef/UserRef';

export function ContestSupervisorAddResultTable({ usernames, insertedSupervisorProfilesMap }) {
  const renderSupervisorsTable = (title, profilesMap) => {
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
        <HTMLTable striped className="table-list-condensed contest-supervisor-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };

  const renderUnknownSupervisorsTable = () => {
    const knownUsernames = [...Object.keys(insertedSupervisorProfilesMap)];
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
        <h5>Unknown users ({unknownUsernames.length})</h5>
        <HTMLTable striped className="table-list-condensed contest-supervisor-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };

  return (
    <>
      {renderSupervisorsTable('Added supervisors', insertedSupervisorProfilesMap)}
      {renderUnknownSupervisorsTable()}
    </>
  );
}
