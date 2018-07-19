import { Card, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { FormattedDate } from '../../../../../../components/FormattedDate/FormattedDate';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { Contest } from '../../../../../../modules/api/uriel/contest';

import './ContestTable.css';

export interface ContestTableProps {
  contests: Contest[];
  buttonIntent: Intent;
}

export class ContestTable extends React.PureComponent<ContestTableProps, {}> {
  render() {
    const { contests } = this.props;
    const list = contests.map(contest => (
      <Link
        key={contest.jid}
        to={`/competition/contests/${contest.id}`}
        className="flex-row justify-content-space-between contest-table-item-container"
      >
        <div>
          <h4 className="contest-table-item-name">{contest.name}</h4>
          <p className="contest-table-item-date">
            <small>
              {this.renderBeginTime(contest)}
              {this.renderDurationSeparator(contest)}
              {this.renderDuration(contest)}
            </small>
          </p>
        </div>
      </Link>
    ));

    return <Card className="contest-table-container">{list}</Card>;
  }

  private renderBeginTime = (contest: Contest) => {
    return <FormattedDate value={contest.beginTime} />;
  };

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
