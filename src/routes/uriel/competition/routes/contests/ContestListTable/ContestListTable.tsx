import { Card, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ButtonLink } from '../../../../../../components/ButtonLink/ButtonLink';
import { FormattedDate } from '../../../../../../components/FormattedDate/FormattedDate';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { Contest } from '../../../../../../modules/api/uriel/contest';

import './ContestListTable.css';

export interface ContestListTableProps {
  contestList: Contest[];
  buttonIntent: Intent;
}

export class ContestListTable extends React.PureComponent<ContestListTableProps, {}> {
  render() {
    const { contestList } = this.props;
    const list = contestList.map(contest => (
      <div key={contest.jid} className="flex-row justify-content-space-between contest-list-item-container">
        <div>
          <h4 className="contest-list-item-name">{contest.name}</h4>
          <p className="contest-list-item-date">
            <small>
              <FormattedDate value={contest.beginTime} />
              {this.renderDurationSeparator(contest)}
              {this.renderDuration(contest)}
            </small>
          </p>
        </div>
        <div className="flex-column contest-list-item-info">
          <div className="flex-row justify-content-flex-end">
            <ButtonLink
              to={`/competition/contests/${contest.id}`}
              intent={this.props.buttonIntent}
              className="contest-list-view-result"
            >
              View
            </ButtonLink>
          </div>
        </div>
      </div>
    ));

    return <Card className="contest-list-container">{list}</Card>;
  }

  private renderDurationSeparator = (contest: Contest) => {
    if (!contest.duration) {
      return null;
    }
    return ' | ';
  };

  private renderDuration = (contest: Contest) => {
    if (!contest.duration) {
      return null;
    }

    return <FormattedDuration value={contest.duration} />;
  };
}
