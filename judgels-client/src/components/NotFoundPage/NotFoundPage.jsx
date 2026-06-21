import { NonIdealState } from '@blueprintjs/core';

import { FullPageLayout } from '../FullPageLayout/FullPageLayout';

export function NotFoundPage() {
  return (
    <FullPageLayout>
      <NonIdealState description="The page you are looking for does not exist." />
    </FullPageLayout>
  );
}
