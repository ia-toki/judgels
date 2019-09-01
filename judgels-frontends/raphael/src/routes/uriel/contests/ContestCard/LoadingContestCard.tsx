import { Classes } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';

export const LoadingContestCard = () => (
  <ContentCard>
    <h4 className={Classes.SKELETON}>This is a placeholder for a long contest name</h4>
    <p className={Classes.SKELETON}>
      <small>Placeholder for contest date</small>
    </p>
  </ContentCard>
);
