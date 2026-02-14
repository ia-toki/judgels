import { Intent, Tag } from '@blueprintjs/core';

import { ProblemType } from '../../modules/api/sandalphon/problem';
import { useWebPrefs } from '../../modules/webPrefs';

import './ProblemDifficulty.scss';

export default function ProblemDifficulty({ problem, difficulty }) {
  const { hideProblemDifficulty } = useWebPrefs();
  const showProblemDifficulty = !hideProblemDifficulty;

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

  if (!showProblemDifficulty || problem.type === ProblemType.Bundle) {
    return null;
  }

  return (
    <div className="problem-difficulty">
      {renderACStats(difficulty)}
      {renderLevel(difficulty)}
    </div>
  );
}
