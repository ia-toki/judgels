import { PureComponent } from 'react';

import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';
import { FormattedDuration } from '../../../../components/FormattedDuration/FormattedDuration';
import { TimeanddateLink } from '../../../../components/TimeanddateLink/TimeanddateLink';

import './ContestCard.scss';

export class ContestCard extends PureComponent {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentCardLink secondary to={`/contests/${contest.slug}`}>
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
