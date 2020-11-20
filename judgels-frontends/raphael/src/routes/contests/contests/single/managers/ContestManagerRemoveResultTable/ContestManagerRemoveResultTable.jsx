import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { UserRef } from '../../../../../../components/UserRef/UserRef';

export function ContestManagerRemoveResultTable({ usernames, deletedManagerProfilesMap }) {
  const renderManagersTable = (title, profilesMap) => {
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
        <HTMLTable striped className="table-list-condensed contest-manager-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };

  const renderUnknownManagersTable = () => {
    const knownUsernames = Object.keys(deletedManagerProfilesMap);
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
        <h5>Not managers ({unknownUsernames.length})</h5>
        <HTMLTable striped className="table-list-condensed contest-manager-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };

  return (
    <>
      {renderManagersTable('Removed managers', deletedManagerProfilesMap)}
      {renderUnknownManagersTable()}
    </>
  );
}
