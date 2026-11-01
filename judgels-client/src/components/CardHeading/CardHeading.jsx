import { Flex } from '@blueprintjs/labs';

import './CardHeading.scss';

export function CardHeading({ title, action }) {
  return (
    <Flex className="card-heading" justifyContent="space-between" alignItems="center" gap={2}>
      <h3 className="card-heading__title">{title}</h3>
      {action && (
        <Flex className="card-heading__action" alignItems="center">
          {action}
        </Flex>
      )}
    </Flex>
  );
}
