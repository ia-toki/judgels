import { Tag, Intent } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { ProblemType } from '../../modules/api/sandalphon/problem';
import { selectShowProblemDifficulty } from '../../modules/webPrefs/webPrefsSelectors';

import './ProblemDifficulty.scss';

function ProblemDifficulty({ problem, difficulty, showProblemDifficulty }) {
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

const mapStateToProps = state => ({
  showProblemDifficulty: selectShowProblemDifficulty(state),
});

export default connect(mapStateToProps)(ProblemDifficulty);
