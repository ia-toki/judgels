import { Flex } from '@blueprintjs/labs';
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
      <ContentCardLink to={`/contests/${contest.slug}`}>
        <h4>
          <Flex gap={2} justifyContent="space-between">
            {contest.name}
            <div className="contest-card-role">
              <ContestRoleTag role={role} />
            </div>
          </Flex>
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
