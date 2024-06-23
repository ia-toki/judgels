import { Card } from '@blueprintjs/core';
import classNames from 'classnames';

import './ContentCard.scss';

export function ContentCard({ id, secondary, className, elevation, children }) {
  return (
    <Card
      id={id}
      className={classNames(className, 'content-card', {
        'content-card--is-secondary': secondary,
      })}
      elevation={elevation}
    >
      {children}
    </Card>
  );
}
