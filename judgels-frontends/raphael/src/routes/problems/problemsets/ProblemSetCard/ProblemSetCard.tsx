import * as React from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { ProblemSet, ProblemSetProgress } from '../../../../modules/api/jerahmeel/problemSet';

import './ProblemSetCard.css';

export interface ProblemSetCardProps {
  problemSet: ProblemSet;
  archiveDescription?: string;
  progress: ProblemSetProgress;
}

export class ProblemSetCard extends React.PureComponent<ProblemSetCardProps> {
  render() {
    const { problemSet, archiveDescription } = this.props;
    const description = (archiveDescription || '') + (problemSet.description || '');

    return (
      <ContentCardLink to={`/problems/${problemSet.slug}`} className="problemset-card">
        <h4 className="problemset-card__name">
          {problemSet.name}
          {this.renderProgress()}
        </h4>
        {description && (
          <div className="problemset-card__description">
            <HtmlText>{description}</HtmlText>
          </div>
        )}
        {this.renderProgressBar()}
      </ContentCardLink>
    );
  }

  private renderProgress = () => {
    const { progress } = this.props;
    if (!progress || progress.totalProblems === 0) {
      return null;
    }
    const { score, totalProblems } = progress;
    return (
      <ProgressTag large num={score} denom={100 * totalProblems}>
        {score} pts / {totalProblems}
      </ProgressTag>
    );
  };

  private renderProgressBar = () => {
    const { progress } = this.props;
    if (!progress || progress.totalProblems === 0) {
      return null;
    }
    const { score, totalProblems } = progress;
    return <ProgressBar num={score} denom={100 * totalProblems} />;
  };
}
