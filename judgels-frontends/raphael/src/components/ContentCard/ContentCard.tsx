import { Card } from '@blueprintjs/core';
import * as React from 'react';

import './ContentCard.css';

export interface ContentCardProps {
  className?: string;
  children?: any;
}

export const ContentCard = (props: ContentCardProps) => (
  <div className={props.className}>
    <Card className="content-card">{props.children}</Card>
  </div>
);
