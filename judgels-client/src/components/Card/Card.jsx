import { Card as BlueprintCard } from '@blueprintjs/core';
import { Box, Flex } from '@blueprintjs/labs';

import './Card.scss';

export function Card({ className, title, action, actionRightJustified, children }) {
  return (
    <div className={`card ${className}`}>
      <Flex asChild gap={4}>
        <BlueprintCard className="card__title">
          <h3>{title}</h3>
          <Box className="card__title__action" {...(actionRightJustified && { marginLeft: 'auto' })}>
            {action}
          </Box>
        </BlueprintCard>
      </Flex>
      <BlueprintCard className="card__content">
        <Flex flexDirection="column" gap={2}>
          {children}
        </Flex>
      </BlueprintCard>
    </div>
  );
}
