import { HTMLTable, Tag } from '@blueprintjs/core';

import { UserRef } from '../../../../../../components/UserRef/UserRef';
import {
  SupervisorManagementPermission,
  supervisorManagementPermissionShortNamesMap,
} from '../../../../../../modules/api/uriel/contestSupervisor';
import { ContestTab } from '../../../../../../modules/api/uriel/contestWeb';
import { contestIcon } from '../../modules/contestIcon';

import './ContestSupervisorsTable.scss';

const managementPermissionTabsMap = {
  [SupervisorManagementPermission.All]: 'ninja',
  [SupervisorManagementPermission.Announcements]: contestIcon[ContestTab.Announcements],
  [SupervisorManagementPermission.Problems]: contestIcon[ContestTab.Problems],
  [SupervisorManagementPermission.Submissions]: contestIcon[ContestTab.Submissions],
  [SupervisorManagementPermission.Clarifications]: contestIcon[ContestTab.Clarifications],
  [SupervisorManagementPermission.Teams]: contestIcon[ContestTab.Teams],
  [SupervisorManagementPermission.Scoreboard]: contestIcon[ContestTab.Scoreboard],
  [SupervisorManagementPermission.Files]: contestIcon[ContestTab.Files],
};

export function ContestSupervisorsTable({ supervisors, profilesMap }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-user">User</th>
          <th className="col-management-permission">Management permissions</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const sortedSupervisors = supervisors.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedSupervisors.map(supervisor => (
      <tr key={supervisor.userJid}>
        <td>
          <UserRef profile={profilesMap[supervisor.userJid]} />
        </td>
        <td>
          {supervisor.managementPermissions &&
            supervisor.managementPermissions.map(p => (
              <Tag round className="permission-tag" key={p} icon={managementPermissionTabsMap[p]}>
                {supervisorManagementPermissionShortNamesMap[p]}
              </Tag>
            ))}
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed supervisors-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
