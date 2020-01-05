import * as React from 'react';

import { Card } from '../../../../../../components/Card/Card';
import { UserStats } from '../../../../../../modules/api/jerahmeel/user';

export interface ProblemStatsPanelProps {
  userStats: UserStats;
}

export class ProblemStatsPanel extends React.Component<ProblemStatsPanelProps> {
  render() {
    const { userStats } = this.props;
    return (
      <Card title="Problem stats">
        <ul>
          <li>
            Total problem scores: <b>{userStats.totalScores} pts</b>
          </li>
          <li>
            Total problems attempted: <b>{userStats.totalProblemsTried}</b>
            {this.renderVerdictsMap(userStats.totalProblemVerdictsMap)}
          </li>
        </ul>
      </Card>
    );
  }

  private renderVerdictsMap = verdictsMap => {
    return (
      <ul>
        {Object.keys(verdictsMap)
          .sort()
          .map(v => (
            <li key={v}>
              <b>{v}</b>: {verdictsMap[v]}
            </li>
          ))}
      </ul>
    );
  };
}
