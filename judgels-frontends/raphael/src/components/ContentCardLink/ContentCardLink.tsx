import * as React from 'react';
import { Link } from 'react-router-dom';

import { ContentCard } from 'components/ContentCard/ContentCard';

import './ContentCardLink.css';
import { ContestRole } from '../../modules/api/uriel/contestWeb';

export interface ContentCardLinkProps {
  to: string;
  className?: string;
  children?: any;
  role?: ContestRole;
}

export const ContentCardLink = (props: ContentCardLinkProps) => (
  <ContentCard className={props.className}>
    <Link className="content-card-link" to={props.to}>
      <div>{props.children}</div>
    </Link>
  </ContentCard>
);
