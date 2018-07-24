import * as React from 'react';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import ActiveContestsWidget from '../../../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';
import HallOfFameWidget from '../../../widgets/hallOfFame/HallOfFameWidget/HallOfFameWidget';

import './HomePage.css';

export const HomePage = () => (
  <FullPageLayout>
    <div className="home-page-row">
      <div className="home-page-row__left">
        <ActiveContestsWidget />
      </div>
      <div className="home-page-row__right">
        <HallOfFameWidget />
      </div>
    </div>
  </FullPageLayout>
);
