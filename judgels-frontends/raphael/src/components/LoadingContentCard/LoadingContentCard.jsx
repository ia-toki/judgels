import { Classes } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from '../ContentCard/ContentCard';

export const LoadingContentCard = () => (
  <ContentCard>
    <h4 className={Classes.SKELETON}>This is a placeholder for a long content name</h4>
    <p className={Classes.SKELETON}>
      <small>Placeholder for content description</small>
    </p>
  </ContentCard>
);
