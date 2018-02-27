import { Card, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContestList } from '../../../../../../../../modules/api/uriel/contest';

import './ContestListTable.css';
import { ButtonLink } from '../../../../../../../../components/ButtonLink/ButtonLink';

export interface ContestListTableProps {
  contestList: ContestList;
}

export class ContestListTable extends React.Component<ContestListTableProps, {}> {
  render() {
    const { contestList } = this.props;
    const list = contestList.data.map(contest => (
      <div key={contest.jid} className="flex-row justify-content-space-between contest-list-item-container">
        <div>
          <h3>{contest.name}</h3>
        </div>
        <div className="flex-column contest-list-item-info">
          <div className="flex-row justify-content-flex-end">
            <ButtonLink
              to={`/competition/contests/${contest.jid}/scoreboard`}
              intent={Intent.PRIMARY}
              className="contest-list-view-result"
            >
              View scoreboard
            </ButtonLink>
          </div>
        </div>
      </div>
    ));

    return <Card className="contest-list-container">{list}</Card>;
  }
}
