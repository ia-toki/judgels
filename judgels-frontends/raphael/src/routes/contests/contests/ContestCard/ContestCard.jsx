import * as React from 'react';

import { TimeanddateLink } from '../../../../components/TimeanddateLink/TimeanddateLink';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';
import { FormattedDuration } from '../../../../components/FormattedDuration/FormattedDuration';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';

import './ContestCard.css';

export class ContestCard extends React.PureComponent {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentCardLink to={`/contests/${contest.slug}`}>
        <h4 className="contest-card-name">
          {contest.name}
          <div className="contest-card-role">
            <ContestRoleTag role={role} />
          </div>
        </h4>
        <p className="contest-card-date">
          <small>
            {this.renderBeginTime(contest)} | {this.renderDuration(contest)}
          </small>
        </p>
      </ContentCardLink>
    );
  }

  renderBeginTime = contest => {
    return (
      <TimeanddateLink time={contest.beginTime} message={contest.name}>
        <FormattedDate value={contest.beginTime} />
      </TimeanddateLink>
    );
  };

  renderDuration = contest => {
    return <FormattedDuration value={contest.duration} />;
  };
}
