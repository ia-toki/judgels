import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface AdminRemoveResultTableProps {
  usernames: string[];
  deletedAdminProfilesMap: ProfilesMap;
}

export class AdminRemoveResultTable extends React.PureComponent<AdminRemoveResultTableProps> {
  render() {
    return (
      <>
        {this.renderAdminsTable('Removed admins', this.props.deletedAdminProfilesMap)}
        {this.renderUnknownAdminsTable()}
      </>
    );
  }

  private renderAdminsTable = (title: string, profilesMap: ProfilesMap) => {
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
        <table className="bp3-html-table bp3-html-table-striped table-list-condensed uriel-admin-dialog-result-table">
          <tbody>{rows}</tbody>
        </table>
      </>
    );
  };

  private renderUnknownAdminsTable = () => {
    const knownUsernames = Object.keys(this.props.deletedAdminProfilesMap);
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
        <h5>Not admins ({usernames.length})</h5>
        <table className="bp3-html-table bp3-html-table-striped table-list-condensed uriel-admin-dialog-result-table">
          <tbody>{rows}</tbody>
        </table>
      </>
    );
  };
}
