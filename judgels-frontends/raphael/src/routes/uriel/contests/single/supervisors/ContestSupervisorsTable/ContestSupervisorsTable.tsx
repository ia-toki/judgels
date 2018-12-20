import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { ContestSupervisor } from 'modules/api/uriel/contestSupervisor';

import './ContestSupervisorsTable.css';

export interface ContestSupervisorsTableProps {
  supervisors: ContestSupervisor[];
  profilesMap: ProfilesMap;
}

export class ContestSupervisorsTable extends React.PureComponent<ContestSupervisorsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed supervisors-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
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
    const { supervisors: supervisors, profilesMap } = this.props;

    const sortedSupervisors = supervisors.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedSupervisors.map(supervisor => (
      <tr key={supervisor.userJid}>
        <td>
          <UserRef profile={profilesMap[supervisor.userJid]} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
