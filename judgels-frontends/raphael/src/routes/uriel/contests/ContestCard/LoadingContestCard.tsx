import * as React from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';

export const LoadingContestCard = () => (
  <ContentCard>
    <h4 className="pt-skeleton">This is a placeholder for a long contest name</h4>
    <p className="pt-skeleton">
      <small>Placeholder for contest date</small>
    </p>
  </ContentCard>
);
