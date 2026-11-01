import { Flex } from '@blueprintjs/labs';

export function ActionButtons({ justifyContent = 'start', flexWrap = 'wrap', children }) {
  return (
    <Flex justifyContent={justifyContent} flexWrap={flexWrap} gap={1}>
      {children}
    </Flex>
  );
}
