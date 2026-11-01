import { Card as BlueprintCard } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import classNames from 'classnames';

import { CardHeading } from '../CardHeading/CardHeading';

import './Card.scss';

export function Card({ className, title, action, fullHeight, children }) {
  return (
    <BlueprintCard className={classNames('card', className, { 'card--full-height': fullHeight })}>
      <div className="card__title">
        <CardHeading title={title} action={action} />
      </div>
      <div className="card__content">
        <Flex flexDirection="column" gap={2}>
          {children}
        </Flex>
      </div>
    </BlueprintCard>
  );
}
