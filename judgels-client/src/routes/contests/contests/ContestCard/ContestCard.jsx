import { PureComponent } from 'react';

import { ContentRow } from '../../../../components/ContentRow/ContentRow';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';
import { FormattedDuration } from '../../../../components/FormattedDuration/FormattedDuration';
import { TimeanddateLink } from '../../../../components/TimeanddateLink/TimeanddateLink';

export class ContestCard extends PureComponent {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentRow
        to={`/contests/${contest.slug}`}
        title={contest.name}
        meta={
          <>
            <span>{this.renderBeginTime(contest)}</span>
            <span>{this.renderDuration(contest)}</span>
          </>
        }
        status={role && <ContestRoleTag role={role} />}
      />
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
