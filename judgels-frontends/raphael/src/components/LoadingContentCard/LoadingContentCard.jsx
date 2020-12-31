import { Classes } from '@blueprintjs/core';

import { ContentCard } from '../ContentCard/ContentCard';

export function LoadingContentCard() {
  return (
    <ContentCard>
      <h4 className={Classes.SKELETON}>This is a placeholder for a long content name</h4>
      <p className={Classes.SKELETON}>
        <small>Placeholder for content description</small>
      </p>
    </ContentCard>
  );
}
