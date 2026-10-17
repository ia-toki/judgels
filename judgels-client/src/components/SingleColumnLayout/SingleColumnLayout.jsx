import { Flex } from '@blueprintjs/labs';

import './SingleColumnLayout.scss';

export function SingleColumnLayout({ children }) {
  return (
    <Flex className="layout-single-col" flexDirection="column" margin="auto">
      {children}
    </Flex>
  );
}
