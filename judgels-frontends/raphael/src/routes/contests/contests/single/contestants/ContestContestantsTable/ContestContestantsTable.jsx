import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { FormattedDate } from '../../../../../../components/FormattedDate/FormattedDate';
import { ProgressBar } from '../../../../../../components/ProgressBar/ProgressBar';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ProfilesMap } from '../../../../../../modules/api/jophiel/profile';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestContestant } from '../../../../../../modules/api/uriel/contestContestant';
import { VirtualModuleConfig } from '../../../../../../modules/api/uriel/contestModule';

import './ContestContestantsTable.css';

export interface ContestContestantsTableProps {
  contest: Contest;
  virtualModuleConfig?: VirtualModuleConfig;
  contestants: ContestContestant[];
  profilesMap: ProfilesMap;
  now: number;
}

export class ContestContestantsTable extends React.PureComponent<ContestContestantsTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed contestants-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
    const isVirtualContest = !!this.props.virtualModuleConfig;
    return (
      <thead>
        <tr>
          <th className="col-no">#</th>
          <th className="col-user">User</th>
          {isVirtualContest && <th>Virtual Progress</th>}
          {isVirtualContest && <th>Virtual Start Time</th>}
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const { contest, virtualModuleConfig, contestants, profilesMap, now } = this.props;
    const isVirtualContest = !!virtualModuleConfig;

    const sortedContestants = contestants.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';

      const startTime1 = c1.contestStartTime || Number.MAX_SAFE_INTEGER;
      const startTime2 = c2.contestStartTime || Number.MAX_SAFE_INTEGER;

      if (startTime1 !== startTime2) {
        return startTime1 - startTime2;
      }
      return username1.localeCompare(username2);
    });

    const rows = sortedContestants.map((contestant, idx) => (
      <tr key={contestant.userJid}>
        <td>{idx + 1}</td>
        <td>
          <UserRef profile={profilesMap[contestant.userJid]} />
        </td>
        {isVirtualContest && (
          <td className="col-virtual-progress">
            {this.renderVirtualProgress(now, contest, virtualModuleConfig, contestant)}
          </td>
        )}
        {isVirtualContest && <td className="col-virtual-start-time">{this.renderVirtualStartTime(contestant)}</td>}
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  private renderVirtualStartTime = (contestant: ContestContestant) => {
    return contestant.contestStartTime && <FormattedDate value={contestant.contestStartTime} />;
  };

  private renderVirtualProgress = (
    now: number,
    contest: Contest,
    virtualModuleConfig: VirtualModuleConfig,
    contestant: ContestContestant
  ) => {
    if (!contestant.contestStartTime) {
      return null;
    }
    const { virtualDuration } = virtualModuleConfig;
    const contestEndTime = contest.beginTime + contest.duration;
    const num =
      Math.min(virtualDuration, Math.min(now, contestEndTime) - contestant.contestStartTime) +
      Math.max(0, contestant.contestStartTime + virtualDuration - contestEndTime);
    const denom = virtualDuration;

    return <ProgressBar num={num} denom={denom} />;
  };
}
