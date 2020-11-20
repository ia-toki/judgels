import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { UserRef } from '../../../../../../components/UserRef/UserRef';

export function ContestManagersTable({ managers, profilesMap }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-user">User</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const sortedManagers = managers.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedManagers.map(manager => (
      <tr key={manager.userJid}>
        <td>
          <UserRef profile={profilesMap[manager.userJid]} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
