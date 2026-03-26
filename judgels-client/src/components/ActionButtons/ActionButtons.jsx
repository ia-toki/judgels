import { Flex } from '@blueprintjs/labs';

export function ActionButtons({ justifyContent = 'start', children }) {
  return (
    <Flex justifyContent={justifyContent} gap={1}>
      {children}
    </Flex>
  );
}
