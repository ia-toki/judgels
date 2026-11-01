import { Classes } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';

import { ContentList } from '../ContentList/ContentList';
import { useShowAfterDelay } from '../LoadingState/useShowAfterDelay';

import './ContentRow.scss';

export function LoadingContentRow() {
  const showSkeleton = useShowAfterDelay(200);

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
