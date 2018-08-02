import * as React from 'react';

import { ContentCard } from 'components/ContentCard/ContentCard';

export const LoadingContestCard = () => (
  <ContentCard>
    <h4 className="bp3-skeleton">This is a placeholder for a long contest name</h4>
    <p className="bp3-skeleton">
      <small>Placeholder for contest date</small>
    </p>
  </ContentCard>
);
