import { HTMLTable } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';

import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';

export function UsersTable({ users, lastSessionTimesMap }) {
  const rows = users.map(user => (
    <tr key={user.jid}>
      <td>
        <Link to={`/admin/users/${user.jid}`}>{user.username}</Link>
      </td>
      <td>{user.email}</td>
      <td>{lastSessionTimesMap[user.jid] ? <FormattedDate value={lastSessionTimesMap[user.jid]} /> : '-'}</td>
    </tr>
  ));

  return (
    <HTMLTable striped className="table-list">
      <thead>
        <tr>
          <th>Username</th>
          <th>Email</th>
          <th>Last login</th>
        </tr>
      </thead>
      <tbody>{rows}</tbody>
    </HTMLTable>
  );
}
