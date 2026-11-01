import { Classes } from '@blueprintjs/core';

import { ContentCard } from '../ContentCard/ContentCard';
import { useShowAfterDelay } from '../LoadingState/useShowAfterDelay';

export function LoadingContentCard() {
  const showSkeleton = useShowAfterDelay(200);

  if (!showSkeleton) {
    return null;
  }

  return (
    <ContentCard>
      <h4 className={Classes.SKELETON}>This is a placeholder for a long content name</h4>
      <p className={Classes.SKELETON}>
        <small>Placeholder for content description</small>
      </p>
    </ContentCard>
  );
}
