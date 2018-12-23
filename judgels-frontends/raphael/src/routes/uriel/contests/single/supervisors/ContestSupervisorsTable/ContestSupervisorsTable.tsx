import { HTMLTable, Tag, IconName } from '@blueprintjs/core';
import * as React from 'react';

import { UserRef } from 'components/UserRef/UserRef';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { ContestSupervisor } from 'modules/api/uriel/contestSupervisor';
import { supervisorPermissionShortNameMap } from 'modules/api/uriel/contestSupervisor';
import { SupervisorManagementPermission } from 'modules/api/uriel/contestSupervisor';

import { contestIcon } from '../../../modules/contestIcon';

import './ContestSupervisorsTable.css';

const supervisorPermissionIconMap = {
  [SupervisorManagementPermission.All]: 'ninja',
  [SupervisorManagementPermission.Announcement]: contestIcon.Announcement,
  [SupervisorManagementPermission.Problem]: contestIcon.Problems,
  [SupervisorManagementPermission.Submission]: contestIcon.Submissions,
  [SupervisorManagementPermission.Clarification]: contestIcon.Clarifications,
  [SupervisorManagementPermission.Team]: contestIcon.Team,
  [SupervisorManagementPermission.Scoreboard]: contestIcon.Scoreboard,
  [SupervisorManagementPermission.File]: contestIcon.Files,
} as { [key: string]: IconName };

export interface ContestSupervisorsTableProps {
  supervisors: ContestSupervisor[];
  profilesMap: ProfilesMap;
}

export class ContestSupervisorsTable extends React.PureComponent<ContestSupervisorsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed supervisors-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-user">User</th>
          <th className="col-management-permission">Management permissions</th>
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { supervisors: supervisors, profilesMap } = this.props;

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
              <Tag round className="permission-tag" key={p} icon={supervisorPermissionIconMap[p]}>
                {supervisorPermissionShortNameMap[p]}
              </Tag>
            ))}
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
