import { Card } from '@blueprintjs/core';
import classNames from 'classnames';

import './ContentCard.scss';

export function ContentCard({ className, elevation, children }) {
  return (
    <Card className={classNames(className, 'content-card')} elevation={elevation}>
      {children}
    </Card>
  );
}
