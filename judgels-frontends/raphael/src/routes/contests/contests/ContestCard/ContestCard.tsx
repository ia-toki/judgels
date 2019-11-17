import * as React from 'react';

import { TimeanddateLink } from '../../../../components/TimeanddateLink/TimeanddateLink';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';
import { FormattedDuration } from '../../../../components/FormattedDuration/FormattedDuration';
import { Contest } from '../../../../modules/api/uriel/contest';
import { ContestRole } from '../../../../modules/api/uriel/contestWeb';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';

import './ContestCard.css';

export interface ContestCardProps {
  contest: Contest;
  role: ContestRole;
}

export class ContestCard extends React.PureComponent<ContestCardProps> {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentCardLink to={`/contests/${contest.slug}`}>
        <h3 className="contest-card-name">
          {contest.name}
          <div className="contest-card-role">
            <ContestRoleTag role={role} />
          </div>
        </h3>
        <p className="contest-card-date">
          <small>
            {this.renderBeginTime(contest)} | {this.renderDuration(contest)}
          </small>
        </p>
      </ContentCardLink>
    );
  }

  private renderBeginTime = (contest: Contest) => {
    return (
      <TimeanddateLink time={contest.beginTime} message={contest.name}>
        <FormattedDate value={contest.beginTime} />
      </TimeanddateLink>
    );
  };

  private renderDuration = (contest: Contest) => {
    return <FormattedDuration value={contest.duration} />;
  };
}
