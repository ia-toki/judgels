import { ChevronRight } from '@blueprintjs/icons';

import './BreadcrumbTitle.scss';

export function BreadcrumbTitle({ parent, current }) {
  return (
    <span>
      {parent}
      <ChevronRight className="breadcrumb-title__separator" />
      {current}
    </span>
  );
}
