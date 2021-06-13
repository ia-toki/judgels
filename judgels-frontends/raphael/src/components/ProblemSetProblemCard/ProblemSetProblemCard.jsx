import { Tag, Intent } from '@blueprintjs/core';
import { SmallTick, Star } from '@blueprintjs/icons';

import { ContentCardLink } from '../ContentCardLink/ContentCardLink';
import { VerdictProgressTag } from '../VerdictProgressTag/VerdictProgressTag';
import { ProgressBar } from '../ProgressBar/ProgressBar';
import { ProblemType } from '../../modules/api/sandalphon/problem';

import './ProblemSetProblemCard.scss';

export function ProblemSetProblemCard({ problemSet, problem, showAlias, problemName, hasEditorial, progress, stats }) {
  const renderStats = () => {
    if (problem.type === ProblemType.Bundle || !stats) {
      return null;
    }

    const { totalScores, totalUsersAccepted, totalUsersTried } = stats;

    return (
      <div className="problemset-problem-card__stats">
        {renderDifficultyLevel(totalScores, totalUsersTried)}
        {renderACStats(totalUsersAccepted, totalUsersTried)}
      </div>
    );
  };

  const renderMetadata = () => {
    if (!hasEditorial) {
      return null;
    }

    return (
      <div className="problemset-problem-card__metadata">
        <Tag round intent={Intent.WARNING}>
          editorial <SmallTick />
        </Tag>
      </div>
    );
  };

  const renderDifficultyLevel = (totalScores, totalUsersTried) => {
    if (totalUsersTried === 0) {
      return null;
    }
    const level = (100 - (totalScores + 100) / (totalUsersTried + 2)) / 10;
    return (
      <Tag intent={Intent.PRIMARY} rightIcon={<Star />}>
        level <span className="problemset-problem-card__stats--large">{level.toFixed(1)}</span>
      </Tag>
    );
  };

  const renderACStats = (totalUsersAccepted, totalUsersTried) => {
    if (totalUsersAccepted > 0) {
      return (
        <Tag intent={Intent.NONE}>
          solved by <span className="problemset-problem-card__stats--large">{totalUsersAccepted}</span> /{' '}
          {totalUsersTried} users
        </Tag>
      );
    }
    return null;
  };

  const renderProgress = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }

    const { verdict, score } = progress;
    return <VerdictProgressTag verdict={verdict} score={score} />;
  };

  const renderProgressBar = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }
    return <ProgressBar verdict={progress.verdict} num={progress.score} denom={100} />;
  };

  return (
    <ContentCardLink
      to={`/problems/${problemSet.slug}/${problem.alias}`}
      className="problemset-problem-card"
      elevation={1}
    >
      <h4 data-key="name">
        {showAlias && <>{problem.alias}. </>}
        {problemName}
        {renderProgress()}
      </h4>
      {renderProgressBar()}
      {renderStats()}
      {renderMetadata()}
    </ContentCardLink>
  );
}
