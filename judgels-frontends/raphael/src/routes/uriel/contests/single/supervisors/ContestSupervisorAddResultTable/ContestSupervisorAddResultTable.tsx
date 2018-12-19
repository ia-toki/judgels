import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestSupervisorAddResultTableProps {
  usernames: string[];
  insertedSupervisorProfilesMap: ProfilesMap;
  alreadySupervisorProfilesMap: ProfilesMap;
}

export class ContestSupervisorAddResultTable extends React.PureComponent<ContestSupervisorAddResultTableProps> {
  render() {
    return (
      <>
        {this.renderSupervisorsTable('Added supervisors', this.props.insertedSupervisorProfilesMap)}
        {this.renderSupervisorsTable('Already supervisors', this.props.alreadySupervisorProfilesMap)}
        {this.renderUnknownSupervisorsTable()}
      </>
    );
  }

  private renderSupervisorsTable = (title: string, profilesMap: ProfilesMap) => {
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

  private renderUnknownSupervisorsTable = () => {
    const knownUsernames = [
      ...Object.keys(this.props.insertedSupervisorProfilesMap),
      ...Object.keys(this.props.alreadySupervisorProfilesMap),
    ];
    const usernames = this.props.usernames
      .filter(u => knownUsernames.indexOf(u) === -1)
      .slice()
      .sort((u1, u2) => u1.localeCompare(u2));

    if (usernames.length === 0) {
      return null;
    }

    const rows = usernames.map(username => (
      <tr key={username}>
        <td>{username}</td>
      </tr>
    ));

    return (
      <>
        <h5>Unknown users ({usernames.length})</h5>
        <HTMLTable striped className="table-list-condensed contest-supervisor-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };
}
