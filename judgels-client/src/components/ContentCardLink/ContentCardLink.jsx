import { Link } from 'react-router-dom';

import { ContentCard } from '../ContentCard/ContentCard';

import './ContentCardLink.scss';

export function ContentCardLink({ className, to, elevation, children }) {
  return (
    <ContentCard className={className} elevation={elevation}>
      <Link className="content-card-link" to={to}>
        {children}
      </Link>
    </ContentCard>
  );
}
