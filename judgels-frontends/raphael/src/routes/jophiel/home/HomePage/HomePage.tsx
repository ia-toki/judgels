import * as React from 'react';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import ActiveContestsWidget from '../../../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';

import './HomePage.css';

export const HomePage = () => (
  <FullPageLayout>
    <div className="home-page-row">
      <div className="home-page-row__half">
        <ActiveContestsWidget />
      </div>
    </div>
  </FullPageLayout>
);
