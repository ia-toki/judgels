import * as React from 'react';

import './ContentCard.css';

export interface ContentCardProps {
  className?: string;
  children?: any;
}

export const ContentCard = (props: ContentCardProps) => (
  <div className={props.className}>
    <div className="pt-card content-card">{props.children}</div>
  </div>
);
