import * as React from 'react';
import { Link } from 'react-router-dom';

import { ContentCard } from '../../components/ContentCard/ContentCard';

import './ContentCardLink.css';

export interface ContentCardLinkProps {
  to: string;
  className?: string;
  elevation?: 0 | 1 | 2 | 3 | 4;
  children?: any;
}

export const ContentCardLink = (props: ContentCardLinkProps) => (
  <ContentCard className={props.className} elevation={props.elevation}>
    <Link className="content-card-link" to={props.to}>
      <div>{props.children}</div>
    </Link>
  </ContentCard>
);
