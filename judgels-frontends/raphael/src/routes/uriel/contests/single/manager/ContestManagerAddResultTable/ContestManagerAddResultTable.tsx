import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestManagerAddResultTableProps {
  usernames: string[];
  insertedManagerProfilesMap: ProfilesMap;
  alreadyManagerProfilesMap: ProfilesMap;
}

export class ContestManagerAddResultTable extends React.PureComponent<ContestManagerAddResultTableProps> {
  render() {
    return (
      <>
        {this.renderManagersTable('Added managers', this.props.insertedManagerProfilesMap)}
        {this.renderManagersTable('Already managers', this.props.alreadyManagerProfilesMap)}
        {this.renderUnknownManagersTable()}
      </>
    );
  }

  private renderManagersTable = (title: string, profilesMap: ProfilesMap) => {
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
        <table className="bp3-html-table bp3-html-table-striped table-list-condensed contest-manager-dialog-result-table">
          <tbody>{rows}</tbody>
        </table>
      </>
    );
  };

  private renderUnknownManagersTable = () => {
    const knownUsernames = [
      ...Object.keys(this.props.insertedManagerProfilesMap),
      ...Object.keys(this.props.alreadyManagerProfilesMap),
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
        <table className="bp3-html-table bp3-html-table-striped table-list-condensed contest-manager-dialog-result-table">
          <tbody>{rows}</tbody>
        </table>
      </>
    );
  };
}
