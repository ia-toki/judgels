import { Classes } from '@blueprintjs/core';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';

export function LoadingActiveContestCard() {
  return (
    <ContentCard>
      <h4 className={Classes.SKELETON}>This is a placeholder for a long contest name</h4>
      <p className={Classes.SKELETON}>
        <small>Placeholder for contest date</small>
      </p>
    </ContentCard>
  );
}
