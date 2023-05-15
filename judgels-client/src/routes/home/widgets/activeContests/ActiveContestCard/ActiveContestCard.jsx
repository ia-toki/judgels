import { PureComponent } from 'react';

import { FormattedRelative } from '../../../../../components/FormattedRelative/FormattedRelative';
import { TimeanddateLink } from '../../../../../components/TimeanddateLink/TimeanddateLink';
import { ContentCardLink } from '../../../../../components/ContentCardLink/ContentCardLink';
import { ContestRoleTag } from '../../../../../components/ContestRole/ContestRoleTag';

import './ActiveContestCard.scss';

export class ActiveContestCard extends PureComponent {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentCardLink to={`/contests/${contest.slug}`}>
        <h4 className="active-contest-card-name">
          {contest.name}
          <div className="active-contest-card-role">
            <ContestRoleTag role={role} />
          </div>
        </h4>
        <p className="active-contest-card-date">
          <small>{this.renderBeginTime(contest)}</small>
        </p>
      </ContentCardLink>
    );
  }

  renderBeginTime = contest => {
    let text = <>in progress</>;
    if (new Date().getTime() < contest.beginTime) {
      text = (
        <>
          starts <FormattedRelative value={contest.beginTime} />
        </>
      );
    }
    return (
      <TimeanddateLink time={contest.beginTime} message={contest.name}>
        {text}
      </TimeanddateLink>
    );
  };
}
