import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { ContestManager } from 'modules/api/uriel/contestManager';

export interface ContestManagersTableProps {
  managers: ContestManager[];
  profilesMap: ProfilesMap;
}

export class ContestManagersTable extends React.PureComponent<ContestManagersTableProps> {
  render() {
    return (
      <table className="bp3-html-table bp3-html-table-striped table-list-condensed">
        {this.renderHeader()}
        {this.renderRows()}
      </table>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-user">User</th>
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { managers, profilesMap } = this.props;

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
}
