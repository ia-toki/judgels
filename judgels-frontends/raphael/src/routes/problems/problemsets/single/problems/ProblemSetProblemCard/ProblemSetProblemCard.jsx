import { Tag, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { VerdictProgressTag } from '../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { ProgressBar } from '../../../../../../components/ProgressBar/ProgressBar';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';

import './ProblemSetProblemCard.css';

export function ProblemSetProblemCard({ problemSet, problem, problemName, progress, stats }) {
  const renderStats = () => {
    if (problem.type === ProblemType.Bundle || !stats) {
      return null;
    }

    const { totalScores, totalUsersAccepted, totalUsersTried } = stats;
    const avgScore = Math.ceil(totalScores / (totalUsersTried || 1));

    return (
      <div className="problemset-problem-card__stats">
        {renderAvgScoreStats(avgScore, totalUsersTried)}
        {renderACStats(totalUsersAccepted)}
      </div>
    );
  };

  const renderAvgScoreStats = (avgScore, totalUsersTried) => {
    return (
      <Tag round intent={Intent.PRIMARY}>
        <span className="problemset-problem-card__stats--large">{avgScore}</span> avg score / {totalUsersTried} users
      </Tag>
    );
  };

  const renderACStats = totalUsersAccepted => {
    if (totalUsersAccepted > 0) {
      return (
        <Tag round intent={Intent.PRIMARY}>
          <span className="problemset-problem-card__stats--large">{totalUsersAccepted}</span> solved
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
        {problem.alias}. {problemName}
        {renderProgress()}
      </h4>
      {renderProgressBar()}
      {renderStats()}
    </ContentCardLink>
  );
}
