import { Flex } from '@blueprintjs/labs';

import './SingleColumnLayout.scss';

export function SingleColumnLayout({ children }) {
  return (
    <Flex className="single-column-layout" flexDirection="column" margin="auto">
      {children}
    </Flex>
  );
}
