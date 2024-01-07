import { Card } from '@blueprintjs/core';
import classNames from 'classnames';

import './ContentCard.scss';

export function ContentCard({ id, className, elevation, children }) {
  return (
    <Card id={id} className={classNames(className, 'content-card')} elevation={elevation}>
      {children}
    </Card>
  );
}
