import classNames from 'classnames';
import { Link } from 'react-router-dom';

import './ContentCardLink.scss';

export function ContentCardLink({ className, to, elevation, children }) {
  // This is basically a <ContentCard /> but with div replaced with a.
  return (
    <Link className={`bp6-card bp-elevation-${elevation} content-card content-card-link`} to={to}>
      <div className={classNames('content-card-link__content', className)}>{children}</div>
    </Link>
  );
}
