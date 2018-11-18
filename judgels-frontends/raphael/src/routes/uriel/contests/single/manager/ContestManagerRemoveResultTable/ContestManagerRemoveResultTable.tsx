import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestManagerRemoveResultTableProps {
  usernames: string[];
  deletedManagerProfilesMap: ProfilesMap;
}

export class ContestManagerRemoveResultTable extends React.PureComponent<ContestManagerRemoveResultTableProps> {
  render() {
    return (
      <>
        {this.renderManagersTable('Removed managers', this.props.deletedManagerProfilesMap)}
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
    const knownUsernames = Object.keys(this.props.deletedManagerProfilesMap);
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
        <h5>Not managers ({usernames.length})</h5>
        <table className="bp3-html-table bp3-html-table-striped table-list-condensed contest-manager-dialog-result-table">
          <tbody>{rows}</tbody>
        </table>
      </>
    );
  };
}
