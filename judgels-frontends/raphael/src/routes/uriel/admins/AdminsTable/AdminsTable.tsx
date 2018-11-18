import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Admin } from 'modules/api/uriel/admin';

export interface AdminsTableProps {
  admins: Admin[];
  profilesMap: ProfilesMap;
}

export class AdminsTable extends React.PureComponent<AdminsTableProps> {
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
    const { admins, profilesMap } = this.props;

    const sortedAdmins = admins.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedAdmins.map(admin => (
      <tr key={admin.userJid}>
        <td>
          <UserRef profile={profilesMap[admin.userJid]} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
