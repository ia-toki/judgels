import { PureComponent } from 'react';

import { ContentTile } from '../../../../../components/ContentTile/ContentTile';
import { ContestRoleTag } from '../../../../../components/ContestRole/ContestRoleTag';
import { FormattedRelative } from '../../../../../components/FormattedRelative/FormattedRelative';
import { TimeanddateLink } from '../../../../../components/TimeanddateLink/TimeanddateLink';
import { ContestRole } from '../../../../../modules/api/contestWeb';

export class ActiveContestCard extends PureComponent {
  render() {
    const { contest, role } = this.props;

    return (
      <ContentTile
        to={`/contests/${contest.slug}`}
        title={contest.name}
        description={this.renderBeginTime(contest)}
        footer={role && role !== ContestRole.None && <ContestRoleTag role={role} />}
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
