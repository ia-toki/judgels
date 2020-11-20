import * as React from 'react';
import { Link } from 'react-router-dom';

import { ContentCard } from '../ContentCard/ContentCard';

import './ContentCardLink.css';

export function ContentCardLink({ className, to, elevation, children }) {
  return (
    <ContentCard className={className} elevation={elevation}>
      <Link className="content-card-link" to={to}>
        <div>{children}</div>
      </Link>
    </ContentCard>
  );
}
