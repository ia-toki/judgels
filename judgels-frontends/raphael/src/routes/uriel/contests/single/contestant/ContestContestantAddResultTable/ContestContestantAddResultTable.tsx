import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestContestantAddResultTableProps {
  usernames: string[];
  insertedContestantProfilesMap: ProfilesMap;
  alreadyContestantProfilesMap: ProfilesMap;
}

export class ContestContestantAddResultTable extends React.PureComponent<ContestContestantAddResultTableProps> {
  render() {
    return (
      <>
        {this.renderContestantsTable('Added contestants', this.props.insertedContestantProfilesMap)}
        {this.renderContestantsTable('Already contestants', this.props.alreadyContestantProfilesMap)}
        {this.renderUnknownContestantsTable()}
      </>
    );
  }

  private renderContestantsTable = (title: string, profilesMap: ProfilesMap) => {
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

  private renderUnknownContestantsTable = () => {
    const knownUsernames = [
      ...Object.keys(this.props.insertedContestantProfilesMap),
      ...Object.keys(this.props.alreadyContestantProfilesMap),
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
        <HTMLTable striped className="table-list-condensed contest-contestant-dialog-result-table">
          <tbody>{rows}</tbody>
        </HTMLTable>
      </>
    );
  };
}
