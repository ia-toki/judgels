import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../components/UserRef/UserRef';

import './ContestLogsTable.css';

export function ContestLogsTable({ logs, profilesMap, problemAliasesMap }) {
  const renderHeader = () => {
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

  const renderRows = () => {
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

  return (
    <HTMLTable striped className="table-list-condensed contest-logs-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
