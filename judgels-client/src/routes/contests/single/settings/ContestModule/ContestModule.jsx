import { Flex } from '@blueprintjs/labs';

import './ContestModule.scss';

export function ContestModule({ title, description, action, children }) {
  return (
    <Flex className="contest-module" flexDirection="column" gap={4}>
      <Flex justifyContent="space-between" alignItems="center" gap={4}>
        <div>
          <h5>{title}</h5>
          {description && <small>{description}</small>}
        </div>
        {action}
      </Flex>
      {children}
    </Flex>
  );
}
