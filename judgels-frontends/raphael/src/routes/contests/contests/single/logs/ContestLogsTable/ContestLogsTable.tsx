import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ProfilesMap } from '../../../../../../modules/api/jophiel/profile';
import { ContestLog } from '../../../../../../modules/api/uriel/contestLog';

import './ContestLogsTable.css';

export interface ContestLogsTableProps {
  logs: ContestLog[];
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export class ContestLogsTable extends React.PureComponent<ContestLogsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed contest-logs-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          <th>User</th>
          <th>Event</th>
          <th className="col-prob">Prob</th>
          <th className="col-time">Time</th>
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { logs, profilesMap, problemAliasesMap } = this.props;

    const rows = logs.map(log => (
      <tr key={log.userJid + log.time}>
        <td>
          <UserRef profile={profilesMap[log.userJid]} />
        </td>
        <td>{log.event}</td>
        <td>{problemAliasesMap[log.problemJid]}</td>
        <td>
          <FormattedRelative value={log.time} />{' '}
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
