import { Card } from '@blueprintjs/core';
import * as React from 'react';

import './ContentCard.css';

export interface ContentCardProps {
  className?: string;
  elevation?: 0 | 1 | 2 | 3 | 4;
  children?: any;
}

export const ContentCard = (props: ContentCardProps) => (
  <div className={props.className}>
    <Card className="content-card" elevation={props.elevation}>
      {props.children}
    </Card>
  </div>
);
