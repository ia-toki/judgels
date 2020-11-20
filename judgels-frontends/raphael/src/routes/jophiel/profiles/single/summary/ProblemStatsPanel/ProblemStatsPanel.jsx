import * as React from 'react';

import { Card } from '../../../../../../components/Card/Card';

export function ProblemStatsPanel({ userStats: { totalScores, totalProblemsTried, totalProblemVerdictsMap } }) {
  const renderVerdictsMap = () => {
    return (
      <ul>
        {Object.keys(totalProblemVerdictsMap)
          .sort()
          .map(v => (
            <li key={v}>
              <b>{v}</b>: {totalProblemVerdictsMap[v]}
            </li>
          ))}
      </ul>
    );
  };

  return (
    <Card title="Problem stats">
      <ul>
        <li>
          Total problem scores: <b>{totalScores}</b> pts
        </li>
        <li>
          Total problems attempted: <b>{totalProblemsTried}</b>
          {renderVerdictsMap()}
        </li>
      </ul>
    </Card>
  );
}
