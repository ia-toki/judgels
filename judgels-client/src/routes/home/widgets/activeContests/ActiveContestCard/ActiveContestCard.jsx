import { PureComponent } from 'react';

import { ContentRow } from '../../../../../components/ContentRow/ContentRow';
import { ContestRoleTag } from '../../../../../components/ContestRole/ContestRoleTag';
import { FormattedRelative } from '../../../../../components/FormattedRelative/FormattedRelative';
import { TimeanddateLink } from '../../../../../components/TimeanddateLink/TimeanddateLink';
import { ContestRole } from '../../../../../modules/api/contestWeb';

export class ActiveContestCard extends PureComponent {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentRow
        to={`/contests/${contest.slug}`}
        title={contest.name}
        meta={<span>{this.renderBeginTime(contest)}</span>}
        status={role && role !== ContestRole.None && <ContestRoleTag role={role} />}
      />
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
