import { Card } from '@blueprintjs/core';
import * as React from 'react';

import { ButtonLink } from '../../../../../../components/ButtonLink/ButtonLink';

import './ContestListTable.css';

export const BlankContestListTable = () => (
  <Card className="contest-list-container">
    <div className="flex-row justify-content-space-between contest-list-item-container">
      <div>
        <h4 className="pt-skeleton">This is a placeholder for a long contest name</h4>
        <p className="pt-skeleton">
          <small>Placeholder for contest date</small>
        </p>
      </div>
      <div className="flex-column contest-list-item-info">
        <div className="flex-row justify-content-flex-end">
          <ButtonLink to={`#`} className="contest-list-view-result pt-skeleton">
            View
          </ButtonLink>
        </div>
      </div>
    </div>
  </Card>
);
