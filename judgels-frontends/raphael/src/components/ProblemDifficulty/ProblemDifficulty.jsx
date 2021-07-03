import { Tag, Intent } from '@blueprintjs/core';

import { ProblemType } from '../../modules/api/sandalphon/problem';

import './ProblemDifficulty.scss';

export function ProblemDifficulty({ problem, difficulty }) {
  const renderLevel = ({ level }) => {
    if (!level) {
      return null;
    }
    return (
      <Tag intent={Intent.PRIMARY}>
        level <span className="problem-difficulty--large">{level}</span>
      </Tag>
    );
  };

  const renderACStats = ({ stats }) => {
    const { totalUsersAccepted, totalUsersTried } = stats;

    if (totalUsersAccepted > 0) {
      return (
        <Tag intent={Intent.NONE}>
          solved by <span className="problem-difficulty--large">{totalUsersAccepted}</span> / {totalUsersTried}
        </Tag>
      );
    }
    return null;
  };

  if (problem.type === ProblemType.Bundle) {
    return null;
  }

  return (
    <div className="problem-difficulty">
      {renderLevel(difficulty)}
      {renderACStats(difficulty)}
    </div>
  );
}
