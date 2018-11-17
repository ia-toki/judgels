import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { ContestContestant } from 'modules/api/uriel/contestContestant';

import './ContestContestantsTable.css';

export interface ContestContestantsTableProps {
  contestants: ContestContestant[];
  profilesMap: ProfilesMap;
}

export class ContestContestantsTable extends React.PureComponent<ContestContestantsTableProps> {
  render() {
    return (
      <table className="bp3-html-table bp3-html-table-striped table-list-condensed contestants-table">
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
    const { contestants, profilesMap } = this.props;

    const sortedContestants = contestants.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedContestants.map(contestant => (
      <tr key={contestant.userJid}>
        <td>
          <UserRef profile={profilesMap[contestant.userJid]} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
