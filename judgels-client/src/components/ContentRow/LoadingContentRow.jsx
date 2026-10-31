import { Classes } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import { useEffect, useState } from 'react';

import { ContentList } from '../ContentList/ContentList';

import './ContentRow.scss';

export function LoadingContentRow() {
  const [showSkeleton, setShowSkeleton] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setShowSkeleton(true), 200);
    return () => clearTimeout(timer);
  }, []);

  if (!showSkeleton) {
    return null;
  }

  return (
    <ContentList>
      <Box className="content-row" paddingX={5} paddingY={4}>
        <h4 className={`content-row__title ${Classes.SKELETON}`}>This is a placeholder for a long content name</h4>
      </Box>
    </ContentList>
  );
}
