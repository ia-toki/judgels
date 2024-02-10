import { Card } from '@blueprintjs/core';

import RichStatementText from '../../../../RichStatementText/RichStatementText';

export function ItemStatementCard({ className, config: { statement } }) {
  return (
    <Card className={className}>
      <RichStatementText>{statement}</RichStatementText>
    </Card>
  );
}
