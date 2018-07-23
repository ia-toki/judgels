import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestProblemStatus,
} from '../../../../../../../../modules/api/uriel/contestProblem';

import './ContestContestantProblemCard.css';

export interface ContestContestantProblemCardProps {
  contest: Contest;
  contestantProblem: ContestContestantProblem;
  problemName: string;
}

export class ContestContestantProblemCard extends React.PureComponent<ContestContestantProblemCardProps> {
  render() {
    const { problem } = this.props.contestantProblem;
    const { contest, problemName } = this.props;

    return (
      <ContentCardLink to={`/contests/${contest.id}/problems/${problem.alias}`}>
        <div className="contestant-problem-card__name">
          <span data-key="name">
            {problem.alias}. {problemName}
          </span>
        </div>
        <div data-key="status" className="contestant-problem-card__status secondary-info">
          {this.renderStatus()}
        </div>
        <div className="clearfix" />
      </ContentCardLink>
    );
  }

  private renderStatus = () => {
    const { problem, totalSubmissions } = this.props.contestantProblem;
    if (problem.status === ContestProblemStatus.Closed) {
      return <Tag intent={Intent.DANGER}>CLOSED</Tag>;
    }
    if (problem.submissionsLimit !== 0) {
      return <span>{problem.submissionsLimit - totalSubmissions} submissions left</span>;
    }
    return <div />;
  };
}
