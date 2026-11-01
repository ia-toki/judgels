import { Flex } from '@blueprintjs/labs';

export function ActionButtons({ justifyContent = 'start', children }) {
  return (
    <Flex justifyContent={justifyContent} flexWrap="wrap" gap={1}>
      {children}
    </Flex>
  );
}
