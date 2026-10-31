import { Link } from '@tanstack/react-router';
import classNames from 'classnames';

import './ContentCardLink.scss';

export function ContentCardLink({ className, to, elevation, framed, children }) {
  // This is basically a <ContentCard /> but with div replaced with a.
  return (
    <Link
      className={classNames(`bp6-card bp-elevation-${elevation} content-card content-card-link`, {
        'content-card--framed': framed,
      })}
      to={to}
    >
      <div className={classNames('content-card-link__content', className)}>{children}</div>
    </Link>
  );
}
