import { ChevronRight } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';

import './BreadcrumbTitle.scss';

export function BreadcrumbTitle({ parent, current }) {
  return (
    <Flex asChild alignItems="center" gap={1}>
      <span>
        {parent}
        <ChevronRight className="breadcrumb-title__separator" />
        {current}
      </span>
    </Flex>
  );
}
