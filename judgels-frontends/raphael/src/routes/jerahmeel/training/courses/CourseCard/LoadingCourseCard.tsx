import { Classes } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCard } from 'components/ContentCard/ContentCard';

export const LoadingCourseCard = () => (
  <ContentCard>
    <h4 className={Classes.SKELETON}>This is a placeholder for a long course name</h4>
  </ContentCard>
);
