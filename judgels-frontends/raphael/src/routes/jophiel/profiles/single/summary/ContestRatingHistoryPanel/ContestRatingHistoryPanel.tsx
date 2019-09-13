import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { Card } from '../../../../../../components/Card/Card';
import { ContestLink } from '../../../../../../components/ContestLink/ContestLink';
import { getRatingClass } from '../../../../../../modules/api/jophiel/userRating';
import { ContestRatingHistoryResponse } from '../../../../../../modules/api/uriel/contestRating';

export interface ContestRatingHistoryPanelProps {
  history: ContestRatingHistoryResponse;
}

export class ContestRatingHistoryPanel extends React.PureComponent<ContestRatingHistoryPanelProps> {
  render() {
    return (
      <Card title="Contest rating history" className="profile-summary-card">
        <HTMLTable striped condensed>
          <thead>
            <tr>
              <th>Contest</th>
              <th>New rating</th>
            </tr>
          </thead>
          <tbody>{this.props.history.data.map(this.renderRow)}</tbody>
        </HTMLTable>
      </Card>
    );
  }

  private renderRow = (event, idx) => {
    const contest = this.props.history.contestsMap[event.contestJid];
    return (
      <tr>
        <td>
          <ContestLink contest={contest} />
        </td>
        <td>
          <span className={getRatingClass(event.rating)}>{event.rating.publicRating}</span>
        </td>
      </tr>
    );
  };
}
