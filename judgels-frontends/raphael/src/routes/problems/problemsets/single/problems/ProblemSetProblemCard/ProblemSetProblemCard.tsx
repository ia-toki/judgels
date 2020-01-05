import { Tag, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { VerdictProgressTag } from '../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { ProgressBar } from '../../../../../../components/ProgressBar/ProgressBar';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../../../modules/api/jerahmeel/problemSetProblem';
import { ProblemProgress, ProblemStats } from '../../../../../../modules/api/jerahmeel/problem';

import './ProblemSetProblemCard.css';

export interface ProblemSetProblemCardProps {
  problemSet: ProblemSet;
  problem: ProblemSetProblem;
  problemName: string;
  progress: ProblemProgress;
  stats: ProblemStats;
}

export class ProblemSetProblemCard extends React.PureComponent<ProblemSetProblemCardProps> {
  render() {
    const { problemSet, problem, problemName } = this.props;

    return (
      <ContentCardLink to={`/problems/${problemSet.slug}/${problem.alias}`} className="problemset-problem-card">
        <h4 data-key="name">
          {problem.alias}. {problemName}
          <div className="problemset-problem-card__stats">
            {this.renderProgress()}
            {this.renderStats()}
          </div>
        </h4>
        {this.renderProgressBar()}
      </ContentCardLink>
    );
  }

  private renderStats = () => {
    const { problem, stats } = this.props;
    if (problem.type === ProblemType.Bundle || !stats) {
      return null;
    }

    const { totalScores, totalUsersAccepted, totalUsersTried } = stats;
    const avgScore = totalScores / (totalUsersTried || 1);

    return (
      <>
        <Tag round intent={Intent.PRIMARY}>
          avg: {avgScore} pts
        </Tag>
        <Tag round intent={Intent.PRIMARY}>
          AC: {totalUsersAccepted} / {totalUsersTried} users
        </Tag>
      </>
    );
  };

  private renderProgress = () => {
    const { problem, progress } = this.props;
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }

    const { verdict, score } = progress;
    return <VerdictProgressTag verdict={verdict} score={score} />;
  };

  private renderProgressBar = () => {
    const { problem, progress } = this.props;
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }
    return <ProgressBar verdict={progress.verdict} num={progress.score} denom={100} />;
  };
}
