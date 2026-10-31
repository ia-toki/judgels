import { Flex } from '@blueprintjs/labs';

import { ProgressBar } from '../ProgressBar/ProgressBar';

import './ContentRow.scss';

export function ContentRowProgress({ verdict, num, denom, children }) {
  return (
    <Flex flexDirection="column" alignItems="end" gap={1}>
      {children}
      <ProgressBar className="content-row__progress" verdict={verdict} num={num} denom={denom} />
    </Flex>
  );
}
