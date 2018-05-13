import * as React from 'react';
import { Link } from 'react-router-dom';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestProblemStatus,
} from '../../../../../../../../../../modules/api/uriel/contestProblem';

import './ContestContestantProblemCard.css';
import { Tag } from '@blueprintjs/core';

export interface ContestContestantProblemCardProps {
  contest: Contest;
  contestantProblem: ContestContestantProblem;
  problemName: string;
}

export class ContestContestantProblemCard extends React.Component<ContestContestantProblemCardProps> {
  render() {
    const { problem } = this.props.contestantProblem;
    const { contest, problemName } = this.props;

    return (
      <ContentCard>
        <div className="contestant-problem__name">
          <Link to={`/competition/contests/${contest.id}/problems/${problem.alias}`}>
            <span data-key="name">
              {problem.alias}. {problemName}
            </span>
          </Link>
        </div>
        <div data-key="status" className="contestant-problem__status">
          {this.renderStatus()}
        </div>
        <div className="clearfix" />
      </ContentCard>
    );
  }

  private renderStatus = () => {
    const { problem, totalSubmissions } = this.props.contestantProblem;
    if (problem.status === ContestProblemStatus.Closed) {
      return <Tag>CLOSED</Tag>;
    }
    if (problem.submissionsLimit !== 0) {
      return <span>{problem.submissionsLimit - totalSubmissions} submissions left</span>;
    }
    return <div />;
  };
}
