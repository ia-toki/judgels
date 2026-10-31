import { Classes } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import classNames from 'classnames';

import './ContentTile.scss';

export function ContentTile({ to, title, description, footer }) {
  return (
    <Flex asChild flexDirection="column" justifyContent="space-between" gap={4}>
      <Link className={classNames(Classes.CARD, 'content-tile')} to={to}>
        <Flex flexDirection="column" gap={2}>
          <h4 className="content-tile__title" data-key="name">
            {title}
          </h4>
          {description && <div className="content-tile__description">{description}</div>}
        </Flex>
        {footer && (
          <Flex className="content-tile__footer" alignItems="center" gap={2}>
            {footer}
          </Flex>
        )}
      </Link>
    </Flex>
  );
}
