import * as React from 'react';

import './DetailPageSide.css';
import { Card } from '../../Card/Card';
import { Button, Intent } from '@blueprintjs/core';
import { loremIpsum } from '../../../routes/labs/routes/contestDetail/ContestDetailPage/mockContestDetail';

export const DetailPageSide = () => (
  <div>
    <Card className="detail-page-side__item flex-column" title={'Registration'}>
      <p>{loremIpsum.substring(0, 50)}</p>
      <div className="flex-row justify-content-flex-end">
        <Button intent={Intent.PRIMARY} className="detail-page-side__register">
          Register Now
        </Button>
      </div>
    </Card>
  </div>
);
