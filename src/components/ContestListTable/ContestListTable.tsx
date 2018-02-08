import * as React from 'react';
import { Card, Button, Intent } from '@blueprintjs/core';

import { ContestList } from '../../modules/api/uriel/contest';

import './ContestListTable.css';

export interface ContestListTableProps {
  contestList: ContestList;
}

export class ContestListTable extends React.Component<ContestListTableProps, {}> {
  render() {
    const { contestList } = this.props;
    const list = contestList.data.map((item, id) => (
      <div key={id} className="flex-row justify-content-space-between contest-list-item-container">
        <div>
          <h3>{item.name}</h3>
        </div>
        <div className="flex-column contest-list-item-info">
          <p>Mar 4, 2017 | 2 Division Contest</p>
          <div className="flex-row justify-content-flex-end">
            {/* TODO add on click */}
            <Button intent={Intent.PRIMARY} className="contest-list-view-result">
              View Result
            </Button>
          </div>
        </div>
      </div>
    ));

    return <Card className="contest-list-container">{list}</Card>;
  }
}
