import * as React from 'react';
import { Card } from '@blueprintjs/core';

import { HtmlText } from '../../../../HtmlText/HtmlText';

export function ItemStatementCard({ className, config: { statement } }) {
  return (
    <Card className={className}>
      <HtmlText>{statement}</HtmlText>
    </Card>
  );
}
