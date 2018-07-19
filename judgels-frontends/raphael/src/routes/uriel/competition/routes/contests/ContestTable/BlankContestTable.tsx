import { Card } from '@blueprintjs/core';
import * as React from 'react';

import './ContestTable.css';

export const BlankContestTable = () => (
  <Card className="contest-table-container">
    <div className="flex-row justify-content-space-between contest-table-item-container">
      <div>
        <h4 className="pt-skeleton">This is a placeholder for a long contest name</h4>
        <p className="pt-skeleton">
          <small>Placeholder for contest date</small>
        </p>
      </div>
    </div>
  </Card>
);
